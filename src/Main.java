import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/*
* search any keyword <- string array (ok)
* search only keyword <- string array (ok)
* search indexes of keyword <- single line string (ok)
* search in file and return suggested line based on keyword <- file (ok)
* search only keyword in single string <- single line string (ok)
* search any keyword in single string <- single line string (ok)
* generate keywords from file <- file
* generate keyword from string <- single line string
* generate keywords from string array <- string array
* */

/**
 * Keyword generating algo
 *
 * 1) Remove all stop words from the text( eg for, the, are, is , and etc.)
 * 2) create an array of candidate keywords which are set of words separated by stop words
 * 3) find the frequency of the words.
 * 4) find the degree of the each word. Degree of a word is the number of how many times a word is used by other candidate keywords
 * 5) for every candidate keyword find the total frequency and degree by summing all word's scores.
 * 6) finally degree/frequency gives the score for being keyword.
 *
 */

public class Main {

    public enum OPTION{
        ONLY_KEYWORD,
        ANY_KEY
    }
    private static String regex = "\\s+|,\\s*|\\.\\s*";
    private static List<String> IgnoreList = new ArrayList<String>();
    private static Map<String,Integer> frequencyMap = new HashMap<String,Integer>();

    public static void main(String[] args) {

        List<String> onlyKeyword,fromFile;
        List<Integer> indexes;

        String[] thisIsAStringArray = {"iphone 5", "iphone 5s", "iphone 3s"};

        onlyKeyword = SearchInArrayOfString(thisIsAStringArray,"ip 5",OPTION.ONLY_KEYWORD);
        indexes = GetIndexOfSubstrInString("iphone 5","ip 5");

        for (String a : onlyKeyword){
            System.out.println(a);
        }
        for (Integer a : indexes){
            System.out.println(a);
        }
        System.out.println(SearchInString("iphone 5","ip 5",OPTION.ONLY_KEYWORD));

//        fromFile = SearchInFile("resources/test.txt","ip 7",OPTION.ONLY_KEYWORD);
//        for (String a : fromFile){
//            System.out.println("-> "+a);
//        }
        LoadIgnoreList("resources/stopword.txt");
        GenerateKeywordFromFile("resources/test.txt");
        
        Set<Entry<String, Integer>> set = frequencyMap.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                //return (o1.getValue()).compareTo( o2.getValue() );//Ascending order
                return (o2.getValue()).compareTo( o1.getValue() );//Descending order
            }
        } );
        for(Map.Entry<String, Integer> entry:list){
            System.out.println(entry.getKey()+" ==== "+entry.getValue());
        }
    }
    private static void GenerateKeywordFromFile(String filename){

        File file = new File(filename);
        if(file.isFile()){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                String line;
                assert br != null;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(regex);
                    for (String word : tokens){
                        if(IgnoreList.contains(word.toUpperCase())){
                            continue;
                        }
                        Integer value = frequencyMap.get(word);
                        if(value != null){
                            frequencyMap.put(word,value+1);
                        }else{
                            frequencyMap.put(word,0);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("File not found");
        }
    }
    private static void LoadIgnoreList(String filename){
        File file = new File(filename);
        if(file.isFile()){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                String line;
                assert br != null;
                while ((line = br.readLine()) != null) {
                    IgnoreList.add(line.toUpperCase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            IgnoreList.add("File not found");
        }
    }
    private static List<String> SearchInFile(String filename, String keyword, OPTION flag   ){

        List<String> temp = new ArrayList<String>();
        File file = new File(filename);
        boolean isFound = false;

        if(file.isFile()){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                String line;
                assert br != null;
                while ((line = br.readLine()) != null) {
                    if(flag == OPTION.ONLY_KEYWORD){
                        isFound = SearchOnlyKeyword(line,keyword);
                    }
                    if(flag == OPTION.ANY_KEY){
                        isFound = SearchAnyKeyword(line,keyword);
                    }
                    if(isFound){
                        temp.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            temp.add("file not found");
        }
        return temp;
    }
    private static boolean SearchInString(String text, String keyword, OPTION flag){

        int counter = 0;

        String[] keywords = keyword.split(regex);

        text = text.toUpperCase();
        
        if(flag == OPTION.ONLY_KEYWORD){
            for (String key : keywords){
                if(text.contains(key.toUpperCase())){
                    counter++;
                }else{
                    counter--;
                }
            }
            return counter >= keywords.length;
        }
        if(flag == OPTION.ANY_KEY){
            for (String key : keywords){
                if(text.contains(key.toUpperCase())){
                    counter++;
                }
            }
            return counter > 0;
        }
        return false;
    }
    private static List<String> SearchInArrayOfString(String[] text, String keyword, OPTION flag){

        List<String> temp = new ArrayList<String>();

        for (String aText : text) {

            if (flag == OPTION.ONLY_KEYWORD){
                boolean isFound = SearchOnlyKeyword(aText, keyword);
                if(isFound){
                    temp.add(aText);
                }
            }
            if(flag == OPTION.ANY_KEY){
                boolean isFound = SearchAnyKeyword(aText,keyword);
                if(isFound){
                    temp.add(aText);
                }
            }
        }
        return temp;
    }
    private static List<Integer> GetIndexOfSubstrInString(String text,String keyword){

        List<Integer> temp = new ArrayList<Integer>();
        String[] keywords = keyword.split(regex);
        boolean isFound = SearchOnlyKeyword(text,keyword);
        if(isFound){
            for (String aKey : keywords){
                for (int index = -1; (index = text.toUpperCase().indexOf(aKey.toUpperCase(), index + 1)) != -1; index++){
                    temp.add(index);
                }
            }
        }
        return temp;
    }
    private static boolean SearchAnyKeyword(String text, String key){

        int counter = 0;

        text = text.replace(" ","").toUpperCase();
        String[] kewords = key.split(regex);

        for (String aKey : kewords){
            if (text.contains(aKey.toUpperCase())){
                counter++;
            }
        }

        return counter > 0;
    }
    private static boolean SearchOnlyKeyword(String text, String key){

        int counter = 0;

        text = text.replace(" ","").toUpperCase();
        String[] keywords = key.split(regex);

        for (String aKey : keywords){
            if (text.contains(aKey.toUpperCase())){
                counter++;
            }else{
                counter--;
            }
        }
        return counter >= keywords.length;
    }
}
