
/*SRM 456 DIV2 500

In Japanese Chess, there is a piece called Silver. A Silver piece located in cell (x,y) can move to any of the following cells in one step: (x-1,y+1), (x,y+1), (x+1,y+1), (x-1,y-1), (x+1,y-1). In other words, it can move one cell in any of the four diagonal directions, or it can move one cell vertically in the positive y direction. 

Initially, there's a Silver piece in cell (sx,sy) of an infinitely large board. Return the minimal number of steps required to move to cell (gx,gy).
*/
public class SilverDistance {
	public static void main(String[] args) {
        SilverDistance t = new SilverDistance();
        int case1 = t.minSteps(1,0,1,9);
        int case2 = t.minSteps(0,0,-4,3);
        int case3 = t.minSteps(0,0,5,8);
        int case4 = t.minSteps(-487617,826524,892309,-918045);
        int case5 = t.minSteps(-27857,31475,-27857,31475);
        System.out.println(case1);
        System.out.println(case2);
        System.out.println(case3);
        System.out.println(case4);
        System.out.println(case5);
    }

    public int minSteps(int sx, int sy, int gx, int gy) {
        int dx = gx - sx;
        int dy = gy - sy;
        
        if (dy >= dx && dy >= -dx) {
            return Math.abs(dy);
        }
        if (dy >= dx || dy >= -dx) {
            int move = Math.abs(dx);
            if (dy%2!=0) {
                move++;
            }
            return move;
        }
        return step(Math.abs(dx), Math.abs(dy));
    }
    private int step(int a, int b) {
        int c = b-a;
        if (c%2==0) {
            return b;
        }
        return b+2;
    }    
}