import java.util.*;
import java.io.File;
import edu.duke.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //REPLACE WITH YOUR CODE
        StringBuilder sb = new StringBuilder();
        for(int i=whichSlice; i<message.length(); i += totalSlices){
            char ch = message.charAt(i);
            sb.append(ch);
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        //WRITE YOUR CODE HERE
        CaesarCracker cc = new CaesarCracker(mostCommon);
        for(int i=0; i<klength; i++){
            String slice = sliceString(encrypted, i, klength);
            key[i] = cc.getKey(slice);
        }
        return key;
    }
    
    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> dictionary = new HashSet<String>();
        for(String word : fr.lines()){
            word = word.toLowerCase();
            if(!dictionary.contains(word)){
                dictionary.add(word);
            }
        }
        
        return dictionary;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        int count = 0;
        String[] words = message.split("\\W+");
        for(String word : words){
            word = word.toLowerCase();
            if(dictionary.contains(word)) count++;
        }
        //System.out.println(count);
        return count;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        int keyLength = 0;
        int max = 0;
        String decryption = "";
        for(int i=1; i<=100; i++){
            int[] keys = tryKeyLength(encrypted, i, mostCommonCharIn(dictionary));
            VigenereCipher vc = new VigenereCipher(keys);
            String decrypted = vc.decrypt(encrypted);
            int count = countWords(decrypted, dictionary);
            if(count > max){
                max = count;
                keyLength = i;
                decryption = decrypted;
            }
        }
        //System.out.println(keyLength);
        //System.out.println(max);
        return decryption;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        HashMap<Character, Integer> count = new HashMap<Character, Integer>();    
        for(String word : dictionary){
            word = word.toLowerCase();
            for(int i=0; i<word.length(); i++){
                char ch = word.charAt(i);
                if(!count.containsKey(ch)){
                    count.put(ch, 1);
                }
                else {
                    count.put(ch, count.get(ch)+1);
                }
            }
        }
        int max = 0;
        Character mostChar = null;
        for(char ch : count.keySet()){
            int value = count.get(ch);
            if(value > max){
                max = value;
                mostChar = ch;
            }
        }
        
        return mostChar;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
        String decrypted = "";
        String langauge = "";
        int max = 0;
        for(String lang : languages.keySet()){
            System.out.println("Breaking key for langauge:"+lang); 
            HashSet<String> dictionary = languages.get(lang);
            String decryption = breakForLanguage(encrypted, dictionary);
            int count = countWords(decryption, dictionary);
            if(count > max){
                max = count;
                decrypted = decryption;
                langauge =lang;
            }
        }
        System.out.println("langauge is: "+langauge);
        System.out.println(decrypted.substring(0,100));
    }

    public void breakVigenere () {
        //WRITE YOUR CODE HERE
        //System.out.println(sliceString("abcdefghijklm", 0, 3)+"\n"+sliceString("abcdefghijklm", 1, 3)
        //+"\n"+sliceString("abcdefghijklm", 2, 3));
        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        DirectoryResource dr = new DirectoryResource();
        HashMap<String, HashSet<String>> langauges = new HashMap<String, HashSet<String>>();
        for(File f : dr.selectedFiles()){
                FileResource newfr = new FileResource("dictionaries/"+f.getName());
                HashSet<String> dictionary = readDictionary(newfr);
                langauges.put(f.getName(), dictionary);
                //System.out.println("read langauge: "+f.getName());
        }
        System.out.println("Dictionaries reading completed");
        breakForAllLangs(encrypted, langauges);
        //String decrypted = breakForLanguage(encrypted, dictionary);
        //String encrypted = "Hhdiu LVXNEW uxh WKWVCEW, krg k wbbsqa si Mmwcjiqm";
        //int[] keys = tryKeyLength(encrypted, 4, 'e');
        /*for(int i : key){
            System.out.print(i+",");
        }*/
        //VigenereCipher vc = new VigenereCipher(keys);
        //String decrypted = vc.decrypt(encrypted);
        //System.out.println(decrypted.substring(0,100));
        
    }
    
}
