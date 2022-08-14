/**********************************************************
* Name: Determining DNA Health
* Author: Yihan Zhu, Youhan Li, Juliet Sampson
* Date: Aug 07, 2022
* Description: COMS4995 Final Project
**********************************************************/
 
import java.io.*;
import java.util.*;
 
public class final_tri {
 
   TrieNode root = new TrieNode();
 
   class TrieNode {
       private TrieNode[] children;
       private List<Integer> indexes;
       private List<Long> health;
 
       TrieNode() {
           this.children = new TrieNode[26];
           this.indexes = new ArrayList<Integer>();
           this.health = new ArrayList<Long>();
       }
   }
 
   public void add(String word, int index, long health) {   //update the health values when contains multiple indexes
       TrieNode curr = root;
       long pre_Sum = 0l;
       for (int i = 0; i < word.length(); i++) {
           TrieNode node = curr.children[word.charAt(i) - 'a'];
           if (node == null) {
               node = new TrieNode();
               curr.children[word.charAt(i) - 'a'] = node;
           }
           curr = node;
       }
       if (!curr.indexes.isEmpty()) {
           pre_Sum = curr.health.get(curr.indexes.size() - 1);
       }
       curr.indexes.add(index);
       curr.health.add(health + pre_Sum); 
   }
 
   private int getIndex(List<Integer> indexes, int start, int end, int index) {    //obtain the highest index in given range (a sorted list)
       int mid = start + (end - start) / 2;    //use binary search
       if (indexes.get(mid).intValue() == index) {
           return mid;
       } else if (indexes.get(start).intValue() == index) {
           return start;
       } else if (indexes.get(end).intValue() == index) {
           return end;
       } else if (indexes.get(mid).intValue() > index) {
           end = mid;
       } else {
           start = mid;
       } // always return sth, leftside_nearest value or for limit cases
       if (end - start <= 1) {
           if (indexes.get(end).intValue() < index) {
               return end;
           }
           return start;
       } // shrink searching range
       return getIndex(indexes, start, end, index);
 
   }
 
   private long getHealth(TrieNode node, int first, int last) {    //obtain the health in given indexes range
       long health = 0l;
       int index_len = node.indexes.size();
       int sIndex, eIndex;
       if (index_len == 0) {
           return 0l;
       }
       sIndex = getIndex(node.indexes, 0, index_len - 1, first - 1);   //obtain the index nearest to first on the left side of first
       eIndex = getIndex(node.indexes, 0, index_len - 1, last);   
       if (node.indexes.get(eIndex).intValue() <= last) {
           health += node.health.get(eIndex);
       }
       if (node.indexes.get(sIndex).intValue() < first) {
        // only if strictly less than first would be deducted(because the getIndex always return an index)
           health -= node.health.get(sIndex);
       }
       return health;     //obtain the health value for current match in Trie by adding values from gene index range(0-last) and minus values from(0-start-1)
   }
 
   public static void main(String[] args) {
        //try {
            //long start = System.nanoTime();
            //FileInputStream fis = new FileInputStream("/Users/zhuyihan/Desktop/4995/final project/input10.txt");
            //try (Scanner in = new Scanner(fis)) {
            try (Scanner in = new Scanner(System.in)) {     //obtain input
               int n = in.nextInt();
               String[] genes = new String[n];
               for (int g_i = 0; g_i < n; g_i++) {
                   genes[g_i] = in.next();
               }
               long[] health = new long[n];
               for (int h_i = 0; h_i < n; h_i++) {
                   health[h_i] = in.nextInt();
               }
 
               long sum = 0l;
               long hmax = Long.MIN_VALUE;
               long hmin = Long.MAX_VALUE;
 
               final_tri Trie = new final_tri();    //initiate the Trie
               for (int i = 0; i < genes.length; i++) {
                   Trie.add(genes[i], i, health[i]);
               }
 
               int s = in.nextInt();
 
               for (int g_i = 0; g_i < s; g_i++) {    //iterate the s DNA strands
                   int from = in.nextInt();
                   int to = in.nextInt();
                   String DNA_strand = in.next();
                   int DNA_s_len = DNA_strand.length();
                   for (int i = 0; i < DNA_s_len; i++) {
                       int charCounter = i;
                       TrieNode node = Trie.root;
                       do {
                           node = node.children[DNA_strand.charAt(charCounter++) - 'a'];
                           if (node == null) {
                               break;    //break if current substr not in the Trie, no larger substr will be, to accelerate
                           }
                           sum += Trie.getHealth(node, from, to);
                       } while (charCounter < DNA_s_len);
                   }
                   hmax = Math.max(hmax, sum);
                   hmin = Math.min(hmin, sum);
                   sum = 0l;
               }
               System.out.format("%d %d\n", hmin, hmax);
               /* 
               System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - start) / 1e6);
               try {
                   FileWriter myWriter = new FileWriter("/Users/zhuyihan/Desktop/4995/final project/output10.txt");
                   myWriter.write("Minimum Total Health: " + hmin + "\n");
                   myWriter.write("Maximum Total Health: " + hmax);
                   myWriter.close();
                   System.out.println("Successfully wrote to the file.");
               } catch (IOException e) {
                   System.out.println("An error occurred.");
                   e.printStackTrace();
               }*/
               
            }
        /*
       } catch (IOException e) {
           //System.out.println("An error occurred.");
           //e.printStackTrace();
        }*/
            
   }
}
 
 
