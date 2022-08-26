import org.junit.Test;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @className: mytest
 * @description: 测试类
 * @author: hone
 * @create: 2022/8/23 21:31
 */
public class mytest {
    @Test
    public void mine(){


    }

    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        ArrayList<String> strings = new ArrayList<>();
//        int num = 0;
//        while (sc.hasNext()) {
//            String next = sc.nextLine();
//            strings.add(next);
//            ++num;
//            if(num >= 3){
//                break;
//            }
//        }
//        strings.stream().forEach(System.out::print);

        Scanner s = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();
        int num = 0;
        while(s.hasNext()){
            //@TODO
            String str = s.nextLine();
            ++num;
            if (num >= 3){
                break;
            }
        }
    }

}
