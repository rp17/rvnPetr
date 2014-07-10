/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static common.misc.CppToJava.half;

public class PriorityQueue {

    /**
     * used to swap two values
     */
    private static <T> void Swap(T a, T b) {
        T temp = a;
        a = b;
        b = temp;
    }

    private static <T extends List> void Swap(T heap, int a, int b) {
        Object o = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, o);
    }

    /**
     * given a heap and a node in the heap, this function moves upwards
     * through the heap swapping elements until the heap is ordered
     */
    private static <T extends Comparable> void ReorderUpwards(List<? extends T> heap, int nd) {
        //move up the heap swapping the elements until the heap is ordered
        while ((nd > 1) && (0 > heap.get(half(nd)).compareTo(heap.get(nd)))) {
            Swap(heap, half(nd), nd);

            nd = half(nd);
        }
    }

    /**
     *  given a heap, the heapsize and a node in the heap, this function
     *  reorders the elements in a top down fashion by moving down the heap
     *  and swapping the current node with the greater of its two children
     *  (provided a child is larger than the current node)
     */
    private static <T extends Comparable> void ReorderDownwards(List<? extends T> heap, int nd, int HeapSize) {
        //move down the heap from node nd swapping the elements until
        //the heap is reordered
        while (2 * nd <= HeapSize) {
            int child = 2 * nd;

            //set child to largest of nd's two children
            if ((child < HeapSize) && (0 > heap.get(child).compareTo(heap.get(child + 1)))) {
                ++child;
            }

            //if this nd is smaller than its child, swap
            if (0 > heap.get(nd).compareTo(heap.get(child))) {
                Swap(heap, child, nd);

                //move the current node down the tree
                nd = child;
            } else {
                break;
            }
        }
    }

    /**
     *  basic heap based priority queue implementation
     */
    public static class PriorityQ<T extends Comparable> {

        private ArrayList<T> m_Heap = new ArrayList<T>();
        private int m_iSize;
        private int m_iMaxSize;

        /** 
         * given a heap and a node in the heap, this function moves upwards
         * through the heap swapping elements until the heap is ordered
         */
        private void ReorderUpwards(ArrayList<T> heap, int nd) {
            //move up the heap swapping the elements until the heap is ordered
            System.out.println("before reorder");
            System.out.println(heap);
            while ((nd > 1) && (0 > heap.get(half(nd)).compareTo(heap.get(nd)))) {
                System.out.println("swap " + half(nd) + " " + nd);
                Swap(heap, half(nd), nd);
                nd = half(nd);
            }
            System.out.println("after reorder");
            System.out.println(heap);
        }

        /**
         * given a heap, the heapsize and a node in the heap, this function
         * reorders the elements in a top down fashion by moving down the heap
         * and swapping the current node with the greater of its two children
         * (provided a child is larger than the current node)
         */
        private void ReorderDownwards(ArrayList<T> heap, int nd, int HeapSize) {
            //move down the heap from node nd swapping the elements until
            //the heap is reordered
            while (2 * nd <= HeapSize) {
                int child = 2 * nd;

                //set child to largest of nd's two children
                if ((child < HeapSize) && (0 > heap.get(child).compareTo(heap.get(child + 1)))) {
                    ++child;
                }

                //if this nd is smaller than its child, swap
                if (0 > heap.get(nd).compareTo(heap.get(child))) {
                    Swap(heap, child, nd);

                    //move the current node down the tree
                    nd = child;
                } else {
                    break;
                }
            }
        }

        /**
         * 
         * @param MaxSize
         * @param fillWith Element with "lowest value" witch will be used for queue inicialisation
         */
        public PriorityQ(int MaxSize, T fillWith) {
            m_Heap.addAll(Collections.nCopies(MaxSize + 1, fillWith));
        }

        public boolean empty() {
            return (m_iSize == 0);
        }

        /**
         * to insert an item into the queue it gets added to the end of the heap
         * and then the heap is reordered
         */
        public void insert(final T item) {

            assert (m_iSize + 1 <= m_iMaxSize);

            ++m_iSize;

            m_Heap.set(m_iSize, item);
            ReorderUpwards(m_Heap, m_iSize);
        }

        //to get the max item the first element is exchanged with the lowest
        //in the heap and then the heap is reordered from the top down. 
        public T pop() {
            Swap(m_Heap, 1, m_iSize);

            ReorderDownwards(m_Heap, 1, m_iSize - 1);

            return m_Heap.get(m_iSize--);
        }

        //so we can take a peek at the first in line
        public final T Peek() {
            return m_Heap.get(1);
        }

        @Override
        public String toString() {
            StringBuilder bf = new StringBuilder();
            for (int i = 0; i < m_Heap.size(); i++) {
                bf.append(m_Heap.get(i));
                bf.append(" ");
                if (i == m_iSize) {
                    bf.append("| ");
                }
            }
            return bf.toString();
        }
    }

    /**
     *  basic 2-way heap based priority queue implementation. This time the priority
     *  is given to the lowest valued key
     */
    public static class PriorityQLow<T extends Comparable> {

        private ArrayList<T> m_Heap = new ArrayList<T>();
        private int m_iSize;
        private int m_iMaxSize;

        /**
         * given a heap and a node in the heap, this function moves upwards
         * through the heap swapping elements until the heap is ordered
         */
        private void ReorderUpwards(ArrayList<T> heap, int nd) {
            //move up the heap swapping the elements until the heap is ordered
            while ((nd > 1) && (0 < heap.get(half(nd)).compareTo(heap.get(nd)))) {
                Swap(heap, half(nd), nd);

                nd = half(nd);
            }
        }

        /**
         * given a heap, the heapsize and a node in the heap, this function
         * reorders the elements in a top down fashion by moving down the heap
         * and swapping the current node with the smaller of its two children
         * (provided a child is larger than the current node)
         */
        private void ReorderDownwards(ArrayList<T> heap, int nd, int HeapSize) {
            //move down the heap from node nd swapping the elements until
            //the heap is reordered
            while (2 * nd <= HeapSize) {
                int child = 2 * nd;

                //set child to smallest of nd's two children
                if ((child < HeapSize) && (0 < heap.get(child).compareTo(heap.get(child + 1)))) {
                    ++child;
                }

                //if this nd is bigger than its child, swap
                if (0 < heap.get(nd).compareTo(heap.get(child))) {
                    Swap(heap, child, nd);

                    //move the current node down the tree
                    nd = child;
                } else {
                    break;
                }
            }
        }

        public PriorityQLow(int MaxSize, T fillWith) {
            m_iMaxSize = MaxSize;
            m_iSize = 0;
            m_Heap.addAll(Collections.nCopies(MaxSize + 1, fillWith));
        }

        public boolean empty() {
            return (m_iSize == 0);
        }

        //to insert an item into the queue it gets added to the end of the heap
        //and then the heap is reordered
        public void insert(final T item) {
            assert (m_iSize + 1 <= m_iMaxSize);

            ++m_iSize;

            m_Heap.set(m_iSize, item);

            ReorderUpwards(m_Heap, m_iSize);
        }

        //to get the max item the first element is exchanged with the lowest
        //in the heap and then the heap is reordered from the top down. 
        public T pop() {
            Swap(m_Heap, 1, m_iSize);

            ReorderDownwards(m_Heap, 1, m_iSize - 1);

            return m_Heap.get(m_iSize--);
        }

        //so we can take a peek at the first in line
        public T peek() {
            return m_Heap.get(1);
        }

        @Override
        public String toString() {
            StringBuilder bf = new StringBuilder();
            for (int i = 0; i < m_Heap.size(); i++) {
                bf.append(m_Heap.get(i));
                bf.append(" ");
                if (i == m_iSize) {
                    bf.append("| ");
                }
            }
            return bf.toString();
        }
    }

    /**
     *  Priority queue based on an index into a set of keys. The queue is
     *  maintained as a 2-way heap.
     *
     *  The priority in this implementation is the lowest valued key
     */
    public static class IndexedPriorityQLow<KeyType extends Comparable> {

        private ArrayList<KeyType> m_vecKeys;
        private ArrayList<Integer> m_Heap = new ArrayList<Integer>();
        private ArrayList<Integer> m_invHeap = new ArrayList<Integer>();
        private int m_iSize;
        private int m_iMaxSize;

        private void Swap(int a, int b) {
            int temp = m_Heap.get(a);
            m_Heap.set(a, m_Heap.get(b));
            m_Heap.set(b, temp);

            //change the handles too
            m_invHeap.set(m_Heap.get(a), a);
            m_invHeap.set(m_Heap.get(b), b);
        }

        private void ReorderUpwards(int nd) {
            //move up the heap swapping the elements until the heap is ordered
            while ((nd > 1) && (0 < m_vecKeys.get(m_Heap.get(half(nd))).compareTo(m_vecKeys.get(m_Heap.get(nd))))) {
                Swap(half(nd), nd);

                nd = half(nd);
            }
        }

        void ReorderDownwards(int nd, int HeapSize) {
            //move down the heap from node nd swapping the elements until
            //the heap is reordered
            while (2 * nd <= HeapSize) {
                int child = 2 * nd;

                //set child to smaller of nd's two children
                if ((child < HeapSize) && (0 < m_vecKeys.get(m_Heap.get(child)).compareTo(m_vecKeys.get(m_Heap.get(child + 1))))) {
                    ++child;
                }

                //if this nd is larger than its child, swap
                if (0 < m_vecKeys.get(m_Heap.get(nd)).compareTo(m_vecKeys.get(m_Heap.get(child)))) {
                    Swap(child, nd);

                    //move the current node down the tree
                    nd = child;
                } else {
                    break;
                }
            }
        }

        /**
         * you must pass the constructor a reference to the std::vector the PQ
         * will be indexing into and the maximum size of the queue.
         */
        public IndexedPriorityQLow(ArrayList<KeyType> keys,
                int MaxSize) {
            m_vecKeys = keys;
            m_iMaxSize = MaxSize;
            m_iSize = 0;

            m_Heap.addAll(Collections.nCopies(MaxSize + 1, 0));
            m_invHeap.addAll(Collections.nCopies(MaxSize + 1, 0));
        }

        public boolean empty() {
            return (m_iSize == 0);
        }

        //to insert an item into the queue it gets added to the end of the heap
        //and then the heap is reordered from the bottom up.
        public void insert(final int idx) {
            assert (m_iSize + 1 <= m_iMaxSize);

            ++m_iSize;

            m_Heap.set(m_iSize, idx);

            m_invHeap.set(idx, m_iSize);

            ReorderUpwards(m_iSize);
        }

        //to get the min item the first element is exchanged with the lowest
        //in the heap and then the heap is reordered from the top down. 
        public int Pop() {
            Swap(1, m_iSize);

            ReorderDownwards(1, m_iSize - 1);

            return m_Heap.get(m_iSize--);
        }

        /**
         * if the value of one of the client key's changes then call this with 
         * the key's index to adjust the queue accordingly
         */
        public void ChangePriority(final int idx) {
            ReorderUpwards(m_invHeap.get(idx));
        }
    }
}