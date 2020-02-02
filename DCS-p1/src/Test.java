


//for testing what is wrong with my wordParser
public class Test{
    public static void main(String[] args){

        String s = "This is a string";
        char [] c = s.toCharArray();

        System.out.println(wordParser(c, 4));

    }

    private static String wordParser(char [] c, int wordNumber){
        String s = "";
        int spaceCount = 0;

        if(wordNumber == 1){
            for(int i = 0; i < c.length && c[i] != ' '; i++){
                s = s+c[i];
            }
        } else {
            try{
                for(int i = 0; spaceCount != wordNumber; i++){
                    if(c[i] == ' ') spaceCount++;
                    else if(spaceCount == wordNumber - 1) s = s + c[i];
                }
            } catch(ArrayIndexOutOfBoundsException e){
                //just continue to let s be output
            }
        }

        return s;
    }

}
