import java.lang.*;

/* P201 - 1.6 */
public class Compression {
    
    public static void main(String[] args){
        Compression c = new Compression();
        String compressedStr = c.compressedStr("aabcccccaaa");
        String compressedStr2 = c.compressedStr("abcdefghijklmnopqrst");
        String compressedStr3 = c.compressedStr("aabbbbcdefff");
        
        System.out.println(compressedStr);
        System.out.println(compressedStr2);
        System.out.println(compressedStr3);
    }
    
    
    public String compressedStr(String s){
        int pre_index = 0, cur_index = 0;
        char pre = s.charAt(pre_index);
        char cur = s.charAt(cur_index);
        StringBuilder strCpr = new StringBuilder();
        
        while (cur_index < s.length() && pre_index < s.length()){
            pre = s.charAt(pre_index);
            cur = s.charAt(cur_index);
            
            if(pre != cur){
                strCpr.append(pre).append(cur_index - pre_index);
                pre_index = cur_index;
            }
            cur_index++;            
        }
        strCpr.append(pre).append(cur_index - pre_index);

        return strCpr.length() > s.length() ? s: strCpr.toString();
    }
}


