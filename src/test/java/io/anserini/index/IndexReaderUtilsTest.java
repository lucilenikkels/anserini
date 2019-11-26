/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.index;

import io.anserini.IndexerTestBase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class IndexReaderUtilsTest extends IndexerTestBase {

  @Test
  public void testTermCounts() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, Long> termCountMap;

    termCountMap = IndexReaderUtils.getTermCounts(reader, "here");
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "more");
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "some");
    assertEquals(Long.valueOf(2), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "test");
    assertEquals(Long.valueOf(1), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(1), termCountMap.get("docFreq"));

    termCountMap = IndexReaderUtils.getTermCounts(reader, "text");
    assertEquals(Long.valueOf(3), termCountMap.get("collectionFreq"));
    assertEquals(Long.valueOf(2), termCountMap.get("docFreq"));
  }

  @Test
  public void testPostingsLists() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    List<IndexReaderUtils.Posting> postingsList;

    // here: (0, 2) [0, 4] (2, 1) [0]
    postingsList = IndexReaderUtils.getPostingsList(reader, "here");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {0, 4}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(2, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {0}, postingsList.get(1).getPositions());

    // more: (0, 1) [7] (1, 1) [0]
    postingsList = IndexReaderUtils.getPostingsList(reader, "more");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {7}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(1, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {0}, postingsList.get(1).getPositions());

    // some: (0, 2) [2, 6]
    postingsList = IndexReaderUtils.getPostingsList(reader, "some");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {2, 6}, postingsList.get(0).getPositions());

    // test: (2, 1) [3]
    postingsList = IndexReaderUtils.getPostingsList(reader, "test");
    assertEquals(1, postingsList.get(0).getTF());
    assertEquals(2, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3}, postingsList.get(0).getPositions());

    // text: (0, 2) [3, 8] (1, 1) [1]
    postingsList = IndexReaderUtils.getPostingsList(reader, "text");
    assertEquals(2, postingsList.get(0).getTF());
    assertEquals(0, postingsList.get(0).getDocid());
    assertArrayEquals(new int[] {3, 8}, postingsList.get(0).getPositions());
    assertEquals(1, postingsList.get(1).getTF());
    assertEquals(1, postingsList.get(1).getDocid());
    assertArrayEquals(new int[] {1}, postingsList.get(1).getPositions());
  }

  @Test
  public void testDocumentVector() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    Map<String, Long> documentVector;

    System.out.println("doc1");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc1");
    assertEquals(Long.valueOf(2), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(2), documentVector.get("some"));
    assertEquals(Long.valueOf(2), documentVector.get("text"));

    System.out.println("doc2");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc2");
    assertEquals(Long.valueOf(1), documentVector.get("more"));
    assertEquals(Long.valueOf(1), documentVector.get("text"));

    System.out.println("doc3");
    documentVector = IndexReaderUtils.getDocumentVector(reader, "doc3");
    assertEquals(Long.valueOf(1), documentVector.get("here"));
    assertEquals(Long.valueOf(1), documentVector.get("test"));
  }

  @Test
  public void testDocidConversion() throws Exception {
    Directory dir = FSDirectory.open(tempDir1);
    IndexReader reader = DirectoryReader.open(dir);

    System.out.println("Converting Lucene Docids...");

    assertEquals("doc1", IndexReaderUtils.convertLuceneDocidToDocid(reader, 0));
    assertEquals("doc2", IndexReaderUtils.convertLuceneDocidToDocid(reader, 1));
    assertEquals("doc3", IndexReaderUtils.convertLuceneDocidToDocid(reader, 2));

    assertEquals(0, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc1"));
    assertEquals(1, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc2"));
    assertEquals(2, IndexReaderUtils.convertDocidToLuceneDocid(reader, "doc3"));
  }
}
