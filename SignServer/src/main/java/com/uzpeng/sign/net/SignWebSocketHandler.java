package com.uzpeng.sign.net;

import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.bo.SignHttpLinkBO;
//import com.uzpeng.sign.net.dto.SignDTO;
import com.uzpeng.sign.net.dto.SignDTO;
import com.uzpeng.sign.util.SerializeUtil;
import com.uzpeng.sign.util.UserMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignWebSocketHandler extends TextWebSocketHandler{
    @Autowired
    private Environment environment;
    private static final Logger logger = LoggerFactory.getLogger(SignWebSocketHandler.class);
    private static final Integer DEFAULT_REFRESH_TIME = 12000;
    private Timer timer = new Timer();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        logger.info("handleTextMessage:"+payload);

        if(!payload.equals("ACK")){  //payload={"time":91,"week":4,"signId":125,"courseId":45,"start":1}
            SignDTO signDTO = SerializeUtil.fromJson(payload, SignDTO.class);

            logger.info("连接建立中>>页面传过来的SignDTO :"+signDTO);

            if (signDTO.getStart().equals(StatusConfig.SIGN_START_FLAG)) {
                timer = new Timer();
                SendSignLinkTask sendSignLinkTask = new SendSignLinkTask(session, signDTO);
                logger.info("***********  start construct-sign-link task .....");
                timer.schedule(sendSignLinkTask, 0, DEFAULT_REFRESH_TIME);
            } else {
                logger.info("***********!!!!!!*******这个应该一直不会被执行  cancel construct-sign-link task, close websocket session ");
                session.close();
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("_+_+_+_+_+_+_+afterConnectionEstablished");
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("_+_+_+_+_+_+_+afterConnectionClosed");
        timer.cancel();
        //根据session的id去获取signId,再根据signId去删除token
        UserMap.removeToken(UserMap.getSignId(session.getId()));
        UserMap.removeSignId(session.getId());
        super.afterConnectionClosed(session, status);
        //users.remove(session.getId());
        //session.close();
        //logger.info("_+_+_+_+_+_+_+session状态:"+session.isOpen());
    }

    //多线程不断向前端发送数据
    //在发给前端，需要使用 URLEncoder进行编码，但是前端发回来的竟然是解码后的。需要注意下
    private void constructSignLink(WebSocketSession session, SignDTO signDTO) throws Exception{

        String randomStr = UUID.randomUUID().toString();
        String sourceStr = randomStr+signDTO.getSignId()+signDTO.getCourseId();

        //对信息 进行加密 为   token
        String token = new String(Base64.getEncoder().encode(Sha512DigestUtils.sha(sourceStr)), "utf-8");

        //https://api.inforsecszu.net/sign/v1/student/sign?token=
        String encodedToken = URLEncoder.encode(token,"utf8");  //URLEncoder 对中文字符进行编码和解码

        String url = environment.getProperty("link.host") + "/v1/student/sign?token="+encodedToken;
        //logger.info("WebSocket 里面的 url:"+url +"   SignId:"+signDTO.getSignId()+"  session.isOpen:"+session.isOpen());

        SignHttpLinkBO signHttpLinkBO = new SignHttpLinkBO();
        signHttpLinkBO.setLink(url);

        //logger.info("encodedToken:"+encodedToken);
        //logger.info("token:"+token);
        UserMap.putToken(signDTO.getSignId(), token);
        UserMap.putSignId(session.getId(), signDTO.getSignId());

        //在这个过程中，前台页面不停的刷新页面，session在不停的关闭和开启，服务器推送数据时，会出现session连接已经被关闭了，但是此时服务器还在给客户端发送消息，就会报错。
        //解决办法是 在发送数据之前先确认 session是否已经打开 使用session.isOpen() 为true 则发送消息。

        if(session.isOpen())
            session.sendMessage(new TextMessage(SerializeUtil.toJson(signHttpLinkBO, SignHttpLinkBO.class)));
    }

    private class SendSignLinkTask extends TimerTask{
        private WebSocketSession session;
        private SignDTO signDTO;

        private SendSignLinkTask(WebSocketSession session, SignDTO signDTO) {
            this.signDTO = signDTO;
            this.session = session;
        }

        @Override
        public void run(){
            try {
                constructSignLink(session, signDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args)throws Exception{


        String randomStr = UUID.randomUUID().toString();
        System.out.println(randomStr);
        String sourceStr = randomStr+14+3+"";

        //对信息 进行加密 为 token
        String token = new String(Base64.getEncoder().encode(Sha512DigestUtils.sha(sourceStr)), "utf-8");
        //String encodedToken = URLEncoder.encode(token,"utf8");
        //String token11 = "+eNe6FbJeJ/xM9Ne4ex7VivPmogBFm0bQ2ux61JlT0iq5eMb+MhHUwehqcuK6X/RovRf7AZtsJ/+vbeHlclCkA==";

        String decodedToken =  URLDecoder.decode(token,"utf8");
        System.out.println("to："+token);
        //System.out.println("en："+encodedToken);
        System.out.println("de："+decodedToken+"  "+token.equals(decodedToken));


       /* HashMap<Integer, String> tokenMap = new HashMap<>();
        tokenMap.put(123,"heheh");
        tokenMap.put(123,"hhaha");
        tokenMap.put(123,"hihi");

        for (Map.Entry<Integer, String> entry : tokenMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
*/
        /**/

    }
}
