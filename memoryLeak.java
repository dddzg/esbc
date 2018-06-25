package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class lack {
    public static void main(String[] args) throws InterruptedException{
        //System.out.println("\n申请前JVM的信息： ");
        //getJVMInfo();


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

            //System.out.println("\n申请后JVM的信息： ");
            //getJVMInfo();

        }
    }
}
