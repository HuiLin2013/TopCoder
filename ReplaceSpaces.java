import java.lang.*;

/* P294 - 1.3 */
public class ReplaceSpaces {
    
    public static void main(String[] args){
        ReplaceSpaces rs = new ReplaceSpaces();
        String rs1 = rs.replaceSpaces("Mr John Smith    ", 13);
        
        System.out.println(rs1);
        
    }
    
    public String replaceSpaces(String str, int length){
        
        StringBuilder str_replace = new StringBuilder();
        //Starting location is zero-based; ending location is not zero-based
        char[] str_array = str.substring(0, length).toCharArray();
        
        for(char c :  str_array){
            if(c == ' '){
                str_replace.append("%20");
            }else{
                str_replace.append(c);
            }
        }
        return str_replace.toString();
    }
}


