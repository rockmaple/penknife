package penknife.segment.util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * @see https://gist.github.com/ansjsun/6304960
 */
public class Word2Vec {

    private Map<String, float[]> wordMap = new HashMap<>();

    private int wordCount;
    private int size;
    private int topNSize = 40;
    private static final int MAX_SIZE = 50;

    /**
     * 加载模型
     *
     * @param path 模型的路径
     * @throws IOException
     */
    public void loadModel(String path) throws IOException {

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
             DataInputStream dis = new DataInputStream(bis)) {

            //读取词数
            wordCount = Integer.parseInt(readString(dis));
            //大小
            size = Integer.parseInt(readString(dis));

            for (int i = 0; i < wordCount; i++) {
                String word = readString(dis);
                float[] vectors = new float[size];
                double len = 0;
                for (int j = 0; j < size; j++) {
                    float vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < vectors.length; j++) {
                    vectors[j] = (float) (vectors[j] / len);
                }
                wordMap.put(word, vectors);
                dis.read();
            }
        }
    }

    /**
     * 得到近义词
     *
     * @param word
     * @return
     */
    public Set<WordEntry> distance(String word) {
        float[] wordVector = getWordVector(word);
        if (wordVector == null) {
            return null;
        }
        Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
        List<WordEntry> wordEntrys = new ArrayList<>(topNSize);
        for (Entry<String, float[]> entry : entrySet) {
            String name = entry.getKey();
            if (name.equals(word)) {
                continue;
            }
            float dist = 0;
            float[] tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<>(wordEntrys);
    }

    /**
     * 近义词
     *
     * @return
     */
    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        float[] wv0 = getWordVector(word0);
        float[] wv1 = getWordVector(word1);
        float[] wv2 = getWordVector(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] wordVector = new float[size];
        for (int i = 0; i < size; i++) {
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];
        }
        float[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<>(topNSize);
        for (Entry<String, float[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<>(wordEntrys);
    }

    private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name, score));
            return;
        }
        float min = Float.MAX_VALUE;
        int minOffset = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffset = i;
            }
        }

        if (score > min) {
            wordsEntrys.set(minOffset, new WordEntry(name, score));
        }

    }

    public class WordEntry implements Comparable<WordEntry> {
        public final String name;
        public final float score;

        public WordEntry(String name, float score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.name + "\t" + score;
        }

        @Override
        public int compareTo(WordEntry o) {
            if (this.score > o.score) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    /**
     * 得到词向量
     *
     * @param word
     * @return
     */
    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public Map<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getSize() {
        return size;
    }


}
