
import java.util.Arrays;
/*SRM 456 DIV2 1000
John has some sticks of various lengths. You are given a int[] sticks, where each element is the length of a single stick. 

He is allowed to perform at most C cuts. With each cut, he chooses one of his sticks and divides it into exactly two sticks of arbitrary positive lengths (the sum of their lengths must equal the length of the original stick). Each of these sticks can then be chosen again when making subsequent cuts. 

After he performs all his cuts, he sorts the sticks in non-increasing order by length and chooses the K-th (1-based) stick. Return the maximal possible length of the stick he chooses. Note that he must perform at least as many cuts as are required to end up with K or more sticks.

*/

public class CutSticks {
    private class CutStick  implements Comparable<CutStick> {
        public int oriLength;
        public int cuts;      
        public double length() {
            return ((double)oriLength)/(cuts+1);
        }
        public int compareTo(CutStick stick) {
            double diff = stick.length() - length();
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            }
            return 0;
        }
        @Override
        public String toString() { 
        	return "";
        }
    }
    public double maxKth(int[] sticks, int C, int K) {
        int stickLength = sticks.length;
        Arrays.sort(sticks);
        int[] convert = new int[stickLength];
        for (int i = 0; i < stickLength; i++) {
            convert[i] = sticks[stickLength-i-1];
        }
        sticks = convert;
        if (K <= stickLength) {
            return sticks[K-1];
        }
        int cuts = C;    
        double sum = 0;
        for (int stick : sticks) {
            sum += stick;
        }       
        double[] a_cuts = new double[stickLength];
        int curcuts = cuts;
        for (int i = 0; i < stickLength; i++) {
            a_cuts[i] = K*sticks[i]/sum;
 
            curcuts -= Math.floor(a_cuts[i]);
        }  
        int[] b_cuts = new int[stickLength];
        for (int i = 0; i < stickLength; i++) {
            if (curcuts > 0) {
                b_cuts[i] = (int)Math.ceil(a_cuts[i]);
                curcuts--;
            } else {
                b_cuts[i] = (int)Math.floor(a_cuts[i]);
            }
        }
        CutStick[] cutsticks = new CutStick[stickLength];
        for (int i = 0; i < stickLength; i++) {
            cutsticks[i] = new CutStick();
            cutsticks[i].oriLength = sticks[i];
            cutsticks[i].cuts = b_cuts[i];
        }
        Arrays.sort(cutsticks);
        for (int i = 0; i < stickLength; i++) {
            K -= cutsticks[i].cuts+1;  
            if (K <= 0) {
                return cutsticks[i].length();
            }
        }
        return -1;
    }
    public static void main(String[] args) {
        CutSticks sticks = new CutSticks();
        double case1 = sticks.maxKth(new int[]{ 5,8 }, 1,1);
        double case2 = sticks.maxKth(new int[]{ 5,8 }, 1,2);
        double case3 = sticks.maxKth(new int[]{ 5,8 }, 1,3);
        double case4 = sticks.maxKth(new int[]{ 1000000000, 1000000000, 1 }, 2, 5);
        double case5 = sticks.maxKth(new int[]{ 76, 594, 17, 6984, 26, 57, 9, 876, 5816, 73, 969, 527, 49 }, 789, 456);
        System.out.println(case1);
        System.out.println(case2);
        System.out.println(case3);
        System.out.println(case4);
        System.out.println(case5);
    }
}