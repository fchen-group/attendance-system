package com.uzpeng.sign.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 */
public class RandomUtil {

    public static <T>  List<T> pickAmountRandomly(List<T> list, int amount){
        if(list.size() <= amount){
            return list;
        }
        List<T> copyList = new ArrayList<>(list);

        List<T> result = new ArrayList<>();

        int size = copyList.size();
        if(size == 0) {
            return result;
        }

        int index = size;
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            int randomNum = random.nextInt(index);
            result.add(copyList.get(randomNum));

            copyList.remove(randomNum);
            index--;
        }

        return result;
    }
}
