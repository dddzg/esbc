package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class memoryLack {
    public static void main(String[] args) throws InterruptedException{

        Map<Integer,List<Integer>> cache = new HashMap<Integer,List<Integer>>();

        int index = 0;
        while(true){
            Thread.sleep(1000);

            int applyNum = (1024/4)*1024*100;
            List<Integer> list = new ArrayList<Integer>(applyNum);

            if(false){
                cache.clear();
            }

            cache.put(index++,list);

        }
    }
}
