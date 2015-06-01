
public class Helpers {
    public static boolean isMin(int number, int[] ints){
        boolean isMin = true;
        for(int i =0; i< ints.length; i++){
            if(number>ints[i]){
                isMin = false;
                break;
            }

        }
        return isMin;
    }
    public static boolean isMax(int number, int[] ints){
        boolean isMax = true;
        for(int i =0; i< ints.length; i++){
            if(number<ints[i]){
                isMax = false;
                break;
            }

        }
        return isMax;
    }
}
