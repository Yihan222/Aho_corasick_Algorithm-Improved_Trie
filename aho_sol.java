import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class aho_sol {

  private static long[] health;
  private static long min = Long.MAX_VALUE;
  private static long max = Long.MIN_VALUE;

  private static final int MAX_LENGTH = 1261408;
  private static int[] failures = new int[MAX_LENGTH];
  private static int[][] children = new int[MAX_LENGTH][26];
  private static int[][] outputs = new int[MAX_LENGTH][];
  private static int counter = 0;

  private static void addString(String str, int outputIndex) {//construct goto and output functions
    int currentNode = 0;
    for (char currentChar : str.toCharArray()) {
      int c = currentChar - 'a';
      if (children[currentNode][c] == 0) {//while not exists, build a new state
        children[currentNode][c] = ++counter;
      }
      currentNode = children[currentNode][c];
    }
    if (outputs[currentNode] == null) {
      outputs[currentNode] = new int[] { outputIndex };
    } else {
      outputs[currentNode] = Arrays.copyOf(outputs[currentNode], outputs[currentNode].length + 1);
      outputs[currentNode][outputs[currentNode].length - 1] = outputIndex;// add the outputindex to the tail while the gene appears multiple times
    }
  }

  private static void computeFailures() {//compute failures
    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < 26; ++i) {
      if (children[0][i] != 0) {
        queue.add(children[0][i]);
      }
      failures[children[0][i]] = 0;
    }

    while (!queue.isEmpty()) {
      int current = queue.poll();
      for (int childrenIndex = 0; childrenIndex < 26; ++childrenIndex) {
        int child = children[current][childrenIndex];
        if (child == 0) {
          children[current][childrenIndex] = children[failures[current]][childrenIndex];
          continue;
        }
        queue.add(child);
        failures[child] = children[failures[current]][childrenIndex];
      }
    }
  }

  private static long occurences(int first, int last, String str) {
    int current = 0;
    long result = 0;
    char[] chars = str.toCharArray();
    for (char c : chars) {
      current = children[current][c - 'a'];
      int failure = current;
      while (failure != 0) {
        if (outputs[failure] != null) {
          int[] output = outputs[failure];
          int min = Arrays.binarySearch(output, first);
           /*
           * This method returns index of the search key, 
           * if it is contained in the array, else it returns (-(insertion point) - 1). 
           * The insertion point is the point at which the key would be inserted into the array: 
           * the index of the first element greater than the key, 
           * or a.length if all elements in the array are less than the specified key.
           */
          int max = Arrays.binarySearch(output, last);
          if (min < 0) {
            min = -min - 1;
          }
          if (max < 0) {
            max = -max - 1;
          }
          while (min > 1 && output[min - 1] == first) {
            --min;
          }
          while (max < output.length && output[max] == last) {
            ++max;
          }
          for (int i = min; i < max; ++i) {
            result += health[output[i]];
          }
        }
        failure = failures[failure];
      }
    }
    return result;
  }

  public static void main(String[] args) {

    try (Scanner in = new Scanner(System.in)) {
      int n = in.nextInt();
      for (int genes_i = 0; genes_i < n; genes_i++) {
        addString(in.next(), genes_i);
      }
      computeFailures();
      health = new long[n];
      for (int health_i = 0; health_i < n; health_i++) {
        health[health_i] = in.nextInt();
      }
      int s = in.nextInt();
      for (int a0 = 0; a0 < s; a0++) {
        long currentResult = occurences(in.nextInt(), in.nextInt(), in.next());
        if (currentResult < min) {
          min = currentResult;
        }
        if (currentResult > max) {
          max = currentResult;
        }
      }
      System.out.println(min + " " + max);
      //System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - start) / 1e6);
    }
  }

}

