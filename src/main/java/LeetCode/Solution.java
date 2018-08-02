package LeetCode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TreeNode {
    int val;
    Solution.TreeNode left;
    Solution.TreeNode right;
    TreeNode(int x) { val = x; }
}

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.uniquePaths(4, 3));
    }

    /**
     * 动态规划 回文子串 p[i,j] = true表示i到j的字符串是一个回文字符串，p[i,i] = flase则表示不是 状态转移方程：
     * p[i,j] = true; find all palindrome with length 1
     * if (p[i] == p[i+1]) {p[i,i+1] = true}; find all palindrome with length 2
     * if (p[i] == p[j]) {p[i][i] == p[i+1, j-1]}; find all other palindrome
     *
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        if (s == null) {
            return s;
        }

        if (s.length() == 1) {
            return s;
        }

        char[] charArray = s.toCharArray();

        int strLength = s.length();
        int longestBegin = 0;
        int maxLen = 1;
        boolean[][] table = new boolean[1000][1000];

        for (int i = 0; i < strLength; i++) {
            table[i][i] = true; // 初始化斜对角
        }

        for (int i = 0; i < strLength - 1; i++) {
            if (charArray[i] == charArray[i + 1]) {
                table[i][i + 1] = true; // 初始化斜对角的上面斜对角
                longestBegin = i;
                maxLen = 2;
            }
        }

        if (strLength >= 3) {
            for (int segmentLength = 3; segmentLength <= strLength; segmentLength++) {
                for (int i = 0; i <= strLength - segmentLength; i++) {
                    int j = i + segmentLength - 1;
                    if (charArray[i] == charArray[j] && table[i + 1][j - 1]) {
                        table[i][j] = true;
                        if (segmentLength > maxLen) {
                            longestBegin = i;
                            maxLen = segmentLength;
                        }
                    }
                }
            }
        }

        System.out.println("begin: " + longestBegin + ", len: " + maxLen + ", strLength: " + strLength);

        return s.substring(longestBegin, longestBegin + maxLen);
    }

    public int totalPalindrome(String s) {
        if (s == null) {
            return 0;
        }

        if (s.length() == 1) {
            return 1;
        }

        int totalPalindrome = 0;
        char[] charArray = s.toCharArray();

        int strLength = s.length();
        boolean[][] table = new boolean[1000][1000];

        for (int i = 0; i < strLength; i++) {
            table[i][i] = true;
            totalPalindrome++;
        }

        for (int i = 0; i < strLength - 1; i++) {
            if (charArray[i] == charArray[i + 1]) {
                table[i][i + 1] = true;
                totalPalindrome++;
            }
        }

        if (strLength >= 3) {
            for (int segmentLength = 3; segmentLength <= strLength; segmentLength++) {
                for (int i = 0; i <= strLength - segmentLength; i++) {
                    int j = i + segmentLength - 1;
                    if (charArray[i] == charArray[j] && table[i + 1][j - 1]) {
                        table[i][j] = true;
                        totalPalindrome++;
                    }
                }
            }
        }

        return totalPalindrome;
    }

    /**
     * 动态规划 回文子序列 p[i,j] = n,表示i到j的字符串中最长回文子序列长度为n
     * p[i,i] = 1;
     * if (p[i] == p[i+1]) {p[i,i+1] = 2};
     * if (p[i] == p[j]) {p[i,j] = p[i+1][j-1] + 2};
     * if (p[i] != p[j]) {p[i,j] = Math.max{p[i+1][j], p[i][j-1]}};
     *
     * @param s
     * @return
     */
    public int longestPalindromeSubseq(String s) {
        if (s == null) {
            return 0;
        }

        if (s.length() == 1) {
            return 1;
        }

        int longestPalindromeSubseq = 1;
        char[] charArray = s.toCharArray();

        int strLength = s.length();
        int[][] table = new int[1000][1000];

        for (int i = 0; i < strLength; i++) {
            table[i][i] = 1;
        }

        for (int i = 0; i < strLength - 1; i++) {
            if (charArray[i] == charArray[i + 1]) {
                table[i][i + 1] = 2;
                longestPalindromeSubseq = 2;
            } else {
                table[i][i + 1] = 1;
            }
        }

        if (strLength >= 3) {
            for (int segmentLength = 3; segmentLength <= strLength; segmentLength++) {
                for (int i = 0; i <= strLength - segmentLength; i++) {
                    int j = i + segmentLength - 1;
                    if (charArray[i] == charArray[j]) {
                        table[i][j] = table[i + 1][j - 1] + 2;
                    } else {
                        table[i][j] = Math.max(table[i + 1][j], table[i][j - 1]);
                    }

                    if (table[i][j] > longestPalindromeSubseq) {
                        longestPalindromeSubseq = table[i][j];
                    }
                }
            }
        }

        return longestPalindromeSubseq;
    }

    /**
     * 动态规划 回文子序列 p[i,j] = n,表示i到j的字符串中回文子序列的个数
     * p[i,i] = 1，p[i,i+1] = 2;
     * if (p[i] != p[j]) {p[i,j] = p[i+1,j] + p[i,j-1] - p[i+1,j-1]};
     * if (p[i] == p[j]) {p[i,j] = 2 * p[i+1,j-1] + x}, x的计算依赖于p[i]，p[i]p[i]是否自p[i+1,j-1]出现过，需要额外的数组记录
     *
     * @param S
     * @return
     */
    public long countPalindromicSubsequences(String S) {
        if (S == null) {
            return 0;
        }
        if (S.length() == 1) {
            return 1;
        }

        char[] charArray = S.toCharArray();
        int strLength = S.length();
        int M = (int) 1e9 + 7;
        int[][] countTable = new int[1000][1000];

        for (int i = 0; i < strLength; i++) {
            countTable[i][i] = 1;
        }

        for (int i = 0; i < strLength - 1; i++) {
            countTable[i][i + 1] = 2;
        }

        if (strLength >= 3) {
            for (int segmentLength = 3; segmentLength <= strLength; segmentLength++) {
                for (int i = 0; i <= strLength - segmentLength; i++) {
                    int j = i + segmentLength - 1;
                    if (charArray[i] == charArray[j]) {
                        int left = i + 1, right = j - 1;
                        while (left <= right && charArray[left] != charArray[i]) ++left;
                        while (left <= right && charArray[right] != charArray[i]) --right;
                        if (left > right) {
                            countTable[i][j] = countTable[i + 1][j - 1] * 2 + 2;
                        } else if (left == right) {
                            countTable[i][j] = countTable[i + 1][j - 1] * 2 + 1;
                        } else {
                            countTable[i][j] = countTable[i + 1][j - 1] * 2 - countTable[left + 1][right - 1];
                        }
                    } else {
                        countTable[i][j] = countTable[i + 1][j] + countTable[i][j - 1] - countTable[i + 1][j - 1];
                    }
                    countTable[i][j] = (countTable[i][j] < 0) ? countTable[i][j] + M : countTable[i][j] % M;
                }
            }
        }

        return countTable[0][strLength - 1];
    }

    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        if (nums.length == 1) {
            return nums[0];
        }

        return maxSubSumRec(nums, 0, nums.length - 1);
    }

    public int maxSubSumRec(int[] array, int left, int right) {
        if (left == right) {
            return array[left];
        }

        int center = (left + right) / 2;
        int maxLeftSum = maxSubSumRec(array, left, center);
        int maxRightSum = maxSubSumRec(array, center + 1, right);

        int maxLeftBorderSum = Integer.MIN_VALUE, leftBorderSum = 0;
        for (int i = center; i >= left; i--) {
            leftBorderSum += array[i];
            if (leftBorderSum > maxLeftBorderSum) {
                maxLeftBorderSum = leftBorderSum;
            }
        }

        int maxRightBorderSum = Integer.MIN_VALUE, rightBorderSum = 0;
        for (int i = center + 1; i <= right; i++) {
            rightBorderSum += array[i];
            if (rightBorderSum > maxRightBorderSum) {
                maxRightBorderSum = rightBorderSum;
            }
        }

        int maxLeftRightSum = maxLeftBorderSum + maxRightBorderSum;

        return maxLeftSum > maxLeftRightSum ? (maxLeftSum > maxRightSum ? maxLeftSum : maxRightSum) : (maxLeftRightSum > maxRightSum ? maxLeftRightSum : maxRightSum);
    }

    /**
     * 有序数组找和为目标值的两个数
     * 二分查找、双指针
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length <= 1) {
            throw new IllegalArgumentException("No two sum solution");
        }

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    public int[] twoSumOrderedNums(int[] numbers, int target) {
        int l = 0, h = numbers.length - 1, sum;
        while ((sum = numbers[l] + numbers[h]) != target && h != l) {
            if (sum > target)
                h = binarySearch(numbers, l + 1, h - 1, target - numbers[l]);
            else if (sum < target)
                l = binarySearch(numbers, l + 1, h - 1, target - numbers[h]);
        }
        return new int[]{l + 1, h + 1};
    }

    private int binarySearch(int[] numbers, int low, int high, int target) {
        while (low < high) {
            int mid = (low + high) / 2;
            if (target == numbers[mid]) {
                return mid;
            } else if (target < numbers[mid]) {
                high = mid;
            } else
                low = mid + 1;
        }
        return high;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /**
     * 广度有效遍历和深度优先遍历
     *
     * @param root
     * @param target
     * @return
     */
    public boolean findTarget(TreeNode root, int target) {
        if (root == null) {
            return false;
        }

        Set<Integer> numSet = new HashSet<>();
        Stack<TreeNode> nodeStack = new Stack<>();
        nodeStack.push(root);
        while (!nodeStack.empty()) {
            TreeNode stackTop = nodeStack.peek();
            if (stackTop.left != null) {
                nodeStack.push(stackTop.left);
                stackTop.left = null;
                continue;
            }

            int value = stackTop.val;
            if (numSet.contains(target - value)) {
                return true;
            } else {
                numSet.add(value);
            }
            nodeStack.pop();

            if (stackTop.right != null) {
                nodeStack.push(stackTop.right);
            }
        }

        return false;
    }

    /**
     * 回文数
     */
    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        }
        int xLength = 0;
        int[] bitNumber = new int[20];
        while (x > 0) {
            int remain = x % 10;
            x /= 10;
            bitNumber[xLength++] = remain;
        }

        for (int i = 0; i < xLength / 2; i++) {
            if (bitNumber[i] != bitNumber[xLength - i - 1]) {
                return false;
            }
        }

        return true;
    }

    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }

        if (head.next.next == null && head.val == head.next.val) {
            return true;
        }

        if (head.next.next == null && head.val != head.next.val) {
            return false;
        }

        ListNode curNode = head;
        ListNode curNode2Time = head;
        ListNode preNode = null;
        ListNode nextNode = head.next;
        while (curNode2Time.next != null && curNode2Time.next.next != null) {
            curNode2Time = curNode2Time.next.next;
            curNode.next = preNode;
            preNode = curNode;
            curNode = nextNode;
            nextNode = nextNode.next;
        }

        if (curNode2Time.next != null) {
            curNode.next = preNode;
            preNode = curNode;
        }

        while (preNode.next != null && nextNode.next != null) {
            if (preNode.val != nextNode.val) {
                return false;
            }
            preNode = preNode.next;
            nextNode = nextNode.next;
        }

        return preNode.val == nextNode.val;
    }

    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        reverseListIter(head);
        return newHead;
    }

    private ListNode reverseListIter(ListNode head) {
        if (head == null || head.next == null) {
            newHead = head;
            return head;
        }

        ListNode pre = reverseListIter(head.next);
        pre.next = head;
        head.next = null;
        return head;
    }

    public List<String> letterCasePermutation(String S) {
        List<String> result = new ArrayList<>();
        if (S == null || S.length() == 0) {
            result.add(S);
            return result;
        }

        stackTrace(result, S.toCharArray(), new StringBuilder(), S.length(), 0);
        return result;
    }

    public void stackTrace(List<String> result, char[] charArray, StringBuilder stringPrefix, int length, int index) {
        if (index >= length) {
            result.add(stringPrefix.toString());
            return;
        }

        char c = charArray[index];
        if (c >= 'a' && c <= 'z') {
            stringPrefix.append(c);
            stackTrace(result, charArray, stringPrefix, length, index + 1);

            stringPrefix.deleteCharAt(index);
            c = (char) (c - 32);
            stringPrefix.append(c);
            stackTrace(result, charArray, stringPrefix, length, index + 1);
        } else if (c >= 'A' && c <= 'Z') {
            stringPrefix.append(c);
            stackTrace(result, charArray, stringPrefix, length, index + 1);

            stringPrefix.deleteCharAt(index);
            c = (char) (c + 32);
            stringPrefix.append(c);
            stackTrace(result, charArray, stringPrefix, length, index + 1);
        } else {
            stringPrefix.append(c);
            stackTrace(result, charArray, stringPrefix, length, index + 1);
        }

        stringPrefix.deleteCharAt(index);
        return;
    }

    /**
     * 求全部子集
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        results.add(new ArrayList<>());
        if (nums == null || nums.length == 0) {
            return results;
        }

        Arrays.sort(nums);
        dfs(nums, 0, results, new ArrayList<>());
        return results;
    }

    void dfs(int[] nums, int startIndex, List<List<Integer>> results, List<Integer> innerResult) {
        if (startIndex == nums.length) {
            return;
        }

        for (int i = startIndex; i < nums.length; i++) {
            if (i != startIndex && nums[i] == nums[i - 1]) {
                continue;
            }
            innerResult.add(nums[i]);
            results.add(new ArrayList<>(innerResult));
            dfs(nums, i + 1, results, new ArrayList<>(innerResult));
            innerResult.remove(innerResult.size() - 1);
        }
    }

    public int maxProfit(int[] prices) {
        if (prices == null || prices.length <= 1) {
            return 0;
        }

        int maxProfit = Integer.MAX_VALUE;
        for (int i = 1; i < prices.length; i++) {
            int diff = prices[i] - prices[i - 1];
            if (diff > 0)
                maxProfit += diff;
        }
        return maxProfit;
    }

    public int maxProfitSub(int[] prices) {
        if (prices == null || prices.length <= 1) {
            return 0;
        }

        int maxProfit = Integer.MAX_VALUE;
        for (int i = 1; i < prices.length; i++) {
            int diff = prices[i] - prices[i - 1];
            if (diff > 0)
                maxProfit += diff;
        }
        return maxProfit;
    }

    int pre = -1;
    int minmum = Integer.MAX_VALUE;

    public int getMinimumDifference(TreeNode root) {
        if (root != null) {
            getMinimumDifference(root.left);

            if (pre != -1) {
                minmum = Math.min(minmum, Math.abs(root.val - pre));
            }
            pre = root.val;

            getMinimumDifference(root.right);
        }

        return minmum;
    }

    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            int firstZeroIndex = -1;
            int firstNoneZeroIndex = -1;
            for (int j = i; j < nums.length; j++) {
                if (nums[j] == 0 && firstZeroIndex == -1) {
                    firstZeroIndex = j;
                }
                if (nums[j] != 0 && firstNoneZeroIndex == -1) {
                    firstNoneZeroIndex = j;
                }

                if (firstZeroIndex != -1 && firstNoneZeroIndex != -1) {
                    break;
                }
            }

            if (firstZeroIndex == -1 || firstNoneZeroIndex == -1) {
                return;
            } else if (firstZeroIndex > firstNoneZeroIndex) {
                continue;
            } else {
                int temp = nums[firstZeroIndex];
                nums[firstZeroIndex] = nums[firstNoneZeroIndex];
                nums[firstNoneZeroIndex] = temp;
            }
        }
    }

    public int removeElement(int[] nums, int val) {
        if (nums == null) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[count] = nums[i];
                if (i != count) {
                    nums[i] = val;
                }
                count++;
            }
        }

        return count;
    }

    public int removeDuplicates(int[] nums) {
        if (nums == null) {
            return 0;
        }

        int headElementsCount = 1;
        int repeatTimes = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[headElementsCount - 1]) {
                nums[headElementsCount++] = nums[i];
                repeatTimes = 1;
            } else {
                if (++repeatTimes <= 2) {
                    nums[headElementsCount++] = nums[i];
                }
            }
        }

        return headElementsCount;
    }

    ListNode newHead = null;
    ListNode preNode = null;

    public ListNode removeElements(ListNode head, int val) {
        if (head == null) {
            return newHead;
        }

        if (head.val != val) {
            if (newHead == null) {
                newHead = head;
            }
            preNode = head;
            removeElements(head.next, val);
        } else {
            if (preNode != null) {
                preNode.next = head.next;
            }
            removeElements(head.next, val);
        }

        return newHead;
    }

    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) {
            return head;
        }

        int length = 1;
        ListNode headNode = head;
        ListNode traverse = head;
        while (traverse.next != null) {
            length++;
            traverse = traverse.next;
        }

        if (k % length == 0) {
            return head;
        }

        traverse.next = head;
        int newHeadPre = length - (k % length);

        int index = 1;
        ListNode findHeadTraverse = head;
        while (index <= newHeadPre) {
            findHeadTraverse = findHeadTraverse.next;
            index++;
        }

        ListNode newHead = findHeadTraverse.next;
        findHeadTraverse.next = null;

        return newHead;
    }

    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        if (n <= 0) {
            return result;
        }

        for (int i = 1; i <= 9; i++) {
            List<Integer> combination = new ArrayList<>();
            combination.add(i);
            combinationSum3(result, combination, k, n, 1 + 1, i, i + 1);
            combination.remove(0);
        }

        return result;
    }

    void combinationSum3(List<List<Integer>> result, List<Integer> combination, int times, int total, int currentTimes, int currentTotal, int startIndex) {
        if (currentTimes > times) {
            return;
        }

        for (int i = startIndex; i <= 9; i++) {
            int tempTotal = currentTotal + i;
            if (currentTimes == times && tempTotal == total) {
                combination.add(i);
                result.add(new ArrayList<>(combination));
                combination.remove(currentTimes - 1);
                return;
            } else if (currentTimes <= times && tempTotal > total) {
                return;
            } else if (currentTimes < times && tempTotal < total) {
                combination.add(i);
                combinationSum3(result, combination, times, total, currentTimes + 1, tempTotal, i + 1);
                combination.remove(currentTimes - 1);
            }
        }
    }

    public int maxChunksToSorted(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 1;
        }

        int length = arr.length;
        int count = 1;
        int[] rightMin = new int[length];
        int leftMax = arr[0];

        rightMin[length - 1] = arr[length - 1];
        for (int i = length - 2; i >= 0; i--) {
            rightMin[i] = Math.min(arr[i], rightMin[i + 1]);
        }

        for (int i = 0; i < length; i++) {
            if (rightMin[i] >= leftMax) {
                count++;
                leftMax = arr[i];
            } else {
                leftMax = Math.max(arr[i], leftMax);
            }
        }

        if (count <= 0) {
            count++;
        }
        return count;
    }

    public void deleteNode(ListNode node) {
        if (node == null) {
            return;
        }

        ListNode preNode = node;
        while (node.next != null) {
            node.val = node.next.val;
            preNode = node;
            node = node.next;
        }
        preNode.next = null;
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (nums2 == null || n <= 0) {
            return;
        }

        int i = m - 1;
        int j = n - 1;
        int totalIndex = m + n - 1;

        while (i >= 0 && j >= 0) {
            nums1[totalIndex--] = nums1[i] > nums2[j] ? nums1[i--] : nums2[j--];
        }

        while (j >= 0) {
            nums1[totalIndex--] = nums2[j--];
        }
    }

    public ListNode sortList(ListNode head) {
        if (head == null) {
            return null;
        }

        if (head.next == null) {
            return head;
        }

        ListNode mid = getMidNode(head);
        ListNode midNext = null;
        if (mid != null) {
            midNext = mid.next;
            mid.next = null;
        }

        return mergeTwoLists(sortList(head), sortList(midNext));
    }

    public ListNode getMidNode(ListNode node) {
        if (node == null) {
            return null;
        }

        if (node.next == null) {
            return node;
        }

        ListNode slow = node;
        ListNode fast = node.next.next;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        }

        if (l1.val <= l2.val) {
            l1.next = mergeTwoLists(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeTwoLists(l1, l2.next);
            return l2;
        }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        int length = lists.length;
        while (length > 1) {
            int mid = (length + 1) / 2;
            for (int i = 0; i < length / 2; i++) {
                lists[i] = mergeTwoListsNoneIter(lists[i], lists[i + mid]);
            }
            length = mid;
        }

        return lists[0];
    }

    public ListNode mergeTwoListsNoneIter(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(-1);

        ListNode currentNode = head;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                currentNode.next = l1;
                l1 = l1.next;
            } else {
                currentNode.next = l2;
                l2 = l2.next;
            }
            currentNode = currentNode.next;
        }

        if (l1 != null) {
            currentNode.next = l1;
        }
        if (l2 != null) {
            currentNode.next = l2;
        }

        return head.next;
    }

    public ListNode insertionSortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        insertionSortListIter(head, head.next);
        return head;
    }

    private void insertionSortListIter(ListNode head, ListNode currentNode) {
        if (currentNode == null) {
            return;
        }

        ListNode listNode = head;
        int tempVal = currentNode.val;
        while (listNode != currentNode) {
            if (tempVal < listNode.val) {
                tempVal ^= listNode.val;
                listNode.val ^= tempVal;
                tempVal ^= listNode.val;
            }
            listNode = listNode.next;
        }
        currentNode.val = tempVal;
        insertionSortListIter(head, currentNode.next);
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();

        List<Integer> combination = new ArrayList<>();
        Arrays.sort(candidates);
        combinationSum(result, combination, candidates, 0, 0, target);

        return result;
    }

    public void combinationSum(List<List<Integer>> result, List<Integer> combination, int[] candidates, int lastChoose, int currentTotal, int target) {
        if (currentTotal == target) {
            result.add(new ArrayList<>(combination));
            return;
        } else if (currentTotal > target) {
            return;
        }

        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] < lastChoose) {
                continue;
            }
            int tempTotal = currentTotal + candidates[i];
            combination.add(candidates[i]);
            combinationSum(result, combination, candidates, candidates[i], tempTotal, target);
            combination.remove(combination.size() - 1);
        }
    }

    /**
     * 1 -> 2 -> 3 -> 4 -> 5, 2 4
     * midHeadNodePre -> 1
     * midHeadNode -> 2
     * midTraverseNode -> 4
     * midTraverseNodeNext -> 5
     *
     * @param head
     * @param m
     * @param n
     * @return
     */
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null || head.next == null) {
            return head;
        }

        int aheadStep = n - m;

        ListNode midTraverseNode = head;
        ListNode midHeadNodePre = new ListNode(-1);
        ListNode midHeadNode = head;

        while (m > 1) {
            midHeadNodePre = midTraverseNode;
            midTraverseNode = midTraverseNode.next;
            midHeadNode = midTraverseNode;
            m--;
        }

        ListNode midTraverseNodePre = midHeadNodePre;
        ListNode midTraverseNodeNext = midTraverseNode.next;
        while (aheadStep > 0 && midTraverseNodeNext != null) {
            aheadStep--;
            midTraverseNodePre = midTraverseNode;
            midTraverseNode = midTraverseNodeNext;
            midTraverseNodeNext = midTraverseNode.next;
            midTraverseNode.next = midTraverseNodePre;
        }

        midHeadNode.next = midTraverseNodeNext;
        midHeadNodePre.next = midTraverseNode;

        return midHeadNodePre.val != -1 ? head : midHeadNodePre.next;
    }

    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) {
            return null;
        } else if (head.next == null) {
            return new TreeNode(head.val);
        }

        ListNode midNode = getMidNode(head);
        ListNode midNodeNext = midNode.next;
        ListNode midNodeNextNext = midNodeNext.next;
        midNode.next = null;
        midNodeNext.next = null;

        TreeNode rootNode = new TreeNode(midNodeNext.val);
        rootNode.left = sortedListToBST(head);
        rootNode.right = sortedListToBST(midNodeNextNext);

        return rootNode;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null) {
            return null;
        }

        return sortedArrayToBST(nums, 0, nums.length - 1);
    }

    public TreeNode sortedArrayToBST(int[] nums, int start, int end) {
        if (start > end) {
            return null;
        } else if (start == end) {
            return new TreeNode(nums[start]);
        } else {
            int mid = (end + start + 1) / 2;
            int midPre = mid - 1;
            int midNext = mid + 1;

            TreeNode rootNode = new TreeNode(nums[mid]);
            rootNode.left = sortedArrayToBST(nums, start, midPre);
            rootNode.right = sortedArrayToBST(nums, midNext, end);

            return rootNode;
        }
    }

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();

        Stack<TreeNode> treeNodeStack = new Stack<>();
        treeNodeStack.push(root);

        while (!treeNodeStack.empty()) {
            TreeNode treeNode = treeNodeStack.pop();
            if (treeNode == null) {
                continue;
            }

            result.add(treeNode.val);

            treeNodeStack.push(treeNode.right);
            treeNode.right = null;
            treeNodeStack.push(treeNode.left);
            treeNode.left = null;
        }

        return result;
    }

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();

        Stack<TreeNode> treeNodeStack = new Stack<>();
        treeNodeStack.push(root);

        while (!treeNodeStack.empty()) {
            TreeNode treeNode = treeNodeStack.peek();
            if (treeNode == null) {
                treeNodeStack.pop();
                continue;
            }

            if (treeNode.left != null) {
                treeNodeStack.push(treeNode.left);
                treeNode.left = null;
                continue;
            }

            result.add(treeNode.val);
            treeNodeStack.pop();

            treeNodeStack.push(treeNode.right);
            treeNode.right = null;
        }

        return result;
    }

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();

        Stack<TreeNode> treeNodeStack = new Stack<>();
        treeNodeStack.push(root);

        while (!treeNodeStack.empty()) {
            TreeNode treeNode = treeNodeStack.peek();
            if (treeNode == null) {
                treeNodeStack.pop();
                continue;
            }

            if (treeNode.right != null || treeNode.left != null) {
                treeNodeStack.push(treeNode.right);
                treeNodeStack.push(treeNode.left);
                treeNode.right = null;
                treeNode.left = null;
                continue;
            }

            result.add(treeNode.val);
            treeNodeStack.pop();
        }

        return result;
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null) {
            return head;
        }

        ListNode nthNode = head;
        while (n >= 1) {
            nthNode = nthNode.next;
            n--;
        }

        ListNode tailHeadNode = head;
        ListNode tailHeadNodePre = new ListNode(-1);
        tailHeadNodePre.next = tailHeadNode;
        while (nthNode != null) {
            tailHeadNodePre = tailHeadNode;
            tailHeadNode = tailHeadNode.next;
            nthNode = nthNode.next;
        }
        tailHeadNodePre.next = tailHeadNode.next;

        return tailHeadNodePre.val == -1 ? tailHeadNodePre.next : head;
    }

    public boolean backspaceCompare(String S, String T) {
        Stack<Character> SStack = new Stack<>();
        if (S != null) {
            for (char c : S.toCharArray()) {
                if ('#' == c) {
                    if (!SStack.empty()) {
                        SStack.pop();
                    }
                } else {
                    SStack.push(c);
                }
            }
        }

        Stack<Character> TStack = new Stack<>();
        if (T != null) {
            for (char c : T.toCharArray()) {
                if ('#' == c) {
                    if (!TStack.empty()) {
                        TStack.pop();
                    }
                } else {
                    TStack.push(c);
                }
            }
        }

        while (!SStack.empty() && !TStack.empty()) {
            if (SStack.pop() != TStack.pop()) {
                return false;
            }
        }

        return SStack.empty() && TStack.empty();
    }

    public void sortColors(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }

        int j = -1;
        int k = -1;
        int m = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                nums[++m] = 2;
                nums[++k] = 1;
                nums[++j] = 0;
            } else if (nums[i] == 1) {
                nums[++m] = 2;
                nums[++k] = 1;
            } else {
                nums[++m] = 2;
            }
        }
    }

    public int largestOverlap(int[][] A, int[][] B) {
        if (A == null || B == null) {
            return 0;
        }

        int maxResult = 0;
        int maxI = 0;
        int maxJ = 0;
        int length = A.length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                int tempResult = 0;
                for (int k = 0; k < length; k++) {
                    for (int l = 0; l < length; l++) {
                        if ((k + i) >= length || (l + j) >= length) {
                            continue;
                        }
                        tempResult += (A[(k + i) % length][(l + j) % length] & B[k][l]);
                        // tempResult += A[i][j] & B[k][l];
                    }
                }
                if (tempResult > maxResult) {
                    maxResult = tempResult;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        System.out.println("Max:" + maxResult + ", maxI:" + maxI + ", maxJ:" + maxJ);
        return maxResult;
    }

    public String findLongestWord(String s, List<String> d) {
        String result = "";

        for (String listString : d) {
            int longLength = result.length();
            int listStringLength = listString.length();
            if (longLength > listStringLength || (longLength == listStringLength && result.compareTo(listString) < 0)) {
                continue;
            }
            if (isValidString(s, listString)) {
                result = listString;
            }
        }

        return result;
    }

    boolean isValidString(String s, String listString) {
        if (s == null || listString == null) {
            return false;
        }

        int sIndex = 0;
        int listStringIndex = 0;

        while (sIndex < s.length() && listStringIndex < listString.length()) {
            if (s.charAt(sIndex) == listString.charAt(listStringIndex)) {
                sIndex++;
                listStringIndex++;
            } else {
                sIndex++;
            }
        }

        return listStringIndex == listString.length();
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || head.next == null || k == 1) {
            return head;
        }

        ListNode newHead = null;
        ListNode segHeadNodePre = new ListNode(-1);
        ListNode segTailNodePre = segHeadNodePre;
        ListNode segTailNode = head;
        ListNode segTailNodeNext = segTailNode.next;
        segTailNodePre.next = segTailNode;
        segTailNode.next = null; // 避免产生环

        while (segTailNode != null) {
            int kCount = k;
            while (kCount-- > 1 && segTailNodeNext != null) {
                segTailNodePre = segTailNode;
                segTailNode = segTailNodeNext;
                segTailNodeNext = segTailNode.next;
                segTailNode.next = segTailNodePre;
            }

            if (kCount <= 0) {
                if (newHead == null) {
                    newHead = segTailNode;
                }
                segTailNodePre = segHeadNodePre.next;
                segHeadNodePre.next.next = segTailNodeNext;
                segHeadNodePre.next = segTailNode;

                if (segTailNodePre.next == null) {
                    break;
                }
                segHeadNodePre = segTailNodePre;
                segTailNode = segTailNodePre.next;
                segTailNodeNext = segTailNode.next;
                segTailNode.next = null;
            } else {
                while (segTailNode != segHeadNodePre.next) {
                    segTailNode.next = segTailNodeNext;
                    segTailNodeNext = segTailNode;
                    segTailNode = segTailNodePre;
                    segTailNodePre = segTailNode.next;
                }
                segTailNode.next = segTailNodeNext;
                if (newHead == null && segHeadNodePre.val == -1) {
                    newHead = segHeadNodePre.next;
                }
                break;
            }
        }

        return newHead;
    }

    public int nthUglyNumber(int n) {
        List<Integer> result = new ArrayList<>();
        result.add(1);
        int index = 1;
        int i2, i3, i5;
        i2 = i3 = i5 = 0;
        while (index < n) {
            result.add(Math.min(2 * result.get(i2), Math.min(3 * result.get(i3), 5 * result.get(i5))));
            if (2 * result.get(i2) == result.get(result.size() - 1)) {
                i2++;
            }
            if (3 * result.get(i3) == result.get(result.size() - 1)) {
                i3++;
            }
            if (5 * result.get(i5) == result.get(result.size() - 1)) {
                i5++;
            }
            index++;
        }

        return result.get(n - 1);
    }

    public boolean isUgly(int num) {
        if (num == 0) {
            return false;
        }
        if (num == 1) {
            return true;
        }

        boolean is2 = false;
        if (num % 2 == 0) {
            is2 = isUgly(num / 2);
        }

        boolean is3 = false;
        if (num % 3 == 0) {
            is3 = isUgly(num / 3);
        }

        boolean is5 = false;
        if (num % 5 == 0) {
            is5 = isUgly(num / 5);
        }

        return is2 || is3 || is5;
    }

    public ListNode swapPairs(ListNode head, int k) {
        ListNode newHeadNode = new ListNode(-1);
        ListNode pre = newHeadNode;
        pre.next = head;

        while (pre.next != null && pre.next.next != null) {
            pre.next.val ^= pre.next.next.val;
            pre.next.next.val ^= pre.next.val;
            pre.next.val ^= pre.next.next.val;
            pre = pre.next.next;
        }
        return newHeadNode.next;
    }

    public String longestWord(String[] words) {
        // 首先构建前缀树的根节点
        PrefixTreeNode root = new PrefixTreeNode();
        root.word = "_";
        // 利用单词数组构建前缀树
        for (String word : words) {
            root.insert(word);
        }

        String[] result = new String[]{""};
        root.dfs(result);

        return result[0];
    }

    class PrefixTreeNode {
        /**
         * 保存插入的字符串
         */
        String word = "";

        /**
         * 对于每一个节点来说，其可以与a-z中所有的其他节点相连，因此用数组来保存节点与其他节点间的联系
         * 如对于wo单词来说，假设树根节点为root。'w' - 'a' = 22，如果root节点对应的数组root.links[22] = null，此时新建一个节点表示root节点和w所对应的节点相连。
         * 如果'o' - 'a' = 14，对于w所对应的节点有w.links[14] = null
         * 同样新建一个节点来表示节点'w'和节点'o'相连
         */
        PrefixTreeNode[] links = new PrefixTreeNode[26];

        /**
         * 实现向前缀树中插入一个单词
         */
        public void insert(String s) {
            char[] chs = s.toCharArray();
            PrefixTreeNode curNode = this;
            for (int i = 0; i < chs.length; i++) {
                int index = chs[i] - 'a';
                if (curNode.links[index] == null) {
                    curNode.links[index] = new PrefixTreeNode();
                }
                // 判断下一个字符
                curNode = curNode.links[index];
            }
            // 当前节点对应的单词为s
            curNode.word = s;
        }

        /**
         * 利用深度优先遍历来进行搜索
         */
        void dfs(String[] result) {
            if (this.word.length() == 0) {
                return;
            }
            for (PrefixTreeNode child : this.links) {
                if (child != null) {
                    child.dfs(result);
                    if (child.word.length() > result[0].length() || (child.word.length() == result[0].length() && child.word.compareTo(result[0]) < 0)) {
                        result[0] = child.word;
                    }
                }
            }
        }
    }

    public String replaceWords(List<String> dict, String sentence) {
        StringBuilder wholeSentence = new StringBuilder();
        StringBuilder everySingleWord = new StringBuilder();

        for (char letter : sentence.toCharArray()) {
            if (letter >= 'a' && letter <= 'z') {
                everySingleWord.append(letter);
            } else {
                String word = everySingleWord.toString();
                String minPrefix = word;
                for (String dictWord : dict) {
                    if (word.startsWith(dictWord)) {
                        if (dictWord.compareTo(minPrefix) < 0) {
                            minPrefix = dictWord;
                        }
                    }
                }

                wholeSentence.append(minPrefix).append(letter);
                everySingleWord = new StringBuilder();
            }
        }

        if (everySingleWord.length() > 0) {
            String word = everySingleWord.toString();
            String minPrefix = word;
            for (String dictWord : dict) {
                if (word.startsWith(dictWord)) {
                    if (dictWord.compareTo(minPrefix) < 0) {
                        minPrefix = dictWord;
                    }
                }
            }

            wholeSentence.append(minPrefix);
        }

        return wholeSentence.toString();
    }

    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        Map<String, Integer> stringPositionMap = new HashMap<>();

        for (String str : strs) {
            char[] stringArray = str.toCharArray();
            Arrays.sort(stringArray);
            String sortedStr = String.valueOf(stringArray);
            if (stringPositionMap.containsKey(sortedStr)) {
                int listIndex = stringPositionMap.get(sortedStr);
                result.get(listIndex).add(str);
            } else {
                List<String> stringList = new ArrayList<>();
                stringList.add(str);
                result.add(stringList);
                stringPositionMap.put(sortedStr, result.size() - 1);
            }
        }

        return result;
    }

    public boolean checkInclusion(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() > s2.length()) {
            return false;
        }

        char[] s2Array = s2.toCharArray();
        char[] s1Array = s1.toCharArray();
        int[] s2LetterArray = new int[26];
        int[] s1LetterArray = new int[26];

        for (int i = 0; i < s1.length(); i++) {
            s2LetterArray[s2Array[i] - 'a']++;
            s1LetterArray[s1Array[i] - 'a']++;
        }

        if (isEqual(s2LetterArray, s1LetterArray)) {
            return true;
        }

        for (int i = 1; i <= s2.length() - s1.length(); i++) {
            s2LetterArray[s2Array[i + s1.length() - 1] - 'a']++;
            s2LetterArray[s2Array[i - 1] - 'a']--;
            if (isEqual(s2LetterArray, s1LetterArray)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEqual(int[] s, int[] t) {
        for (int i = 0; i < s.length; i++) {
            if (s[i] != t[i]) return false;
        }

        return true;
    }

    public String minWindow(String s, String t) {
        int[] srcHash = new int[255];
        // 记录目标字符串每个字母出现次数
        for (int i = 0; i < t.length(); i++) {
            srcHash[t.charAt(i)]++;
        }
        int start = 0, i = 0;
        // 用于记录窗口内每个字母出现次数
        int[] destHash = new int[255];
        int found = 0;
        int begin = -1, end = s.length(), minLength = s.length();
        for (start = i = 0; i < s.length(); i++) {
            // 每来一个字符给它的出现次数加1
            destHash[s.charAt(i)]++;
            // 如果加1后这个字符的数量不超过目标串中该字符的数量，则找到了一个匹配字符
            if (destHash[s.charAt(i)] <= srcHash[s.charAt(i)]) found++;
            // 如果找到的匹配字符数等于目标串长度，说明找到了一个符合要求的子串
            if (found == t.length()) {
                // 将开头没用的都跳过，没用是指该字符出现次数超过了目标串中出现的次数，并把它们出现次数都减1
                while (start < i && destHash[s.charAt(start)] > srcHash[s.charAt(start)]) {
                    destHash[s.charAt(start)]--;
                    start++;
                }
                // 这时候start指向该子串开头的字母，判断该子串长度
                if (i - start < minLength) {
                    minLength = i - start;
                    begin = start;
                    end = i;
                }
                // 把开头的这个匹配字符跳过，并将匹配字符数减1
                destHash[s.charAt(start)]--;
                found--;
                // 子串起始位置加1，我们开始看下一个子串了
                start++;
            }
        }
        // 如果begin没有修改过，返回空
        return begin == -1 ? "" : s.substring(begin, end + 1);
    }

    /**
     * 使用双端队列，队列中最多只存储两个下标
     * 1、当前窗口最大值的下标
     * 2、刚入队列的数值的下标
     *
     * @param nums
     * @param k
     * @return
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || nums.length < k) {
            return new int[0];
        }

        Deque<Integer> posDeque = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];

        int i = 0;
        for (; i < nums.length && i < k - 1; i++) {
            while (!posDeque.isEmpty() && nums[i] >= nums[posDeque.getLast()])
                posDeque.removeLast();
            posDeque.addLast(i);
        }

        for (; i < nums.length; i++) {
            while (!posDeque.isEmpty() && i - posDeque.getFirst() >= k)
                posDeque.removeFirst();

            while (!posDeque.isEmpty() && nums[i] >= nums[posDeque.getLast()])
                posDeque.removeLast();
            posDeque.addLast(i);
            result[i - k + 1] = nums[posDeque.getFirst()];
        }

        return result;
    }

    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null) {
            return 0;
        }

        int currentHead = 0;
        int minLen = nums.length;
        int minHead = -1;
        int currentSum = 0;
        for (int i = 0; i < nums.length; i++) {
            currentSum += nums[i];
            if (currentSum >= s) {
                while ((currentSum - nums[currentHead]) >= s) {
                    currentSum -= nums[currentHead];
                    currentHead++;
                }

                if (minLen >= i - currentHead + 1) {
                    minLen = i - currentHead + 1;
                    minHead = currentHead;
                }

                currentSum -= nums[currentHead];
                currentHead++;
            }
        }

        System.out.println(minHead);
        return minHead > -1 ? minLen : 0;
    }

    /**
     * length[i,j] 表示以A[i],B[j]结尾的最长子串的长度
     *
     * @param A
     * @param B
     * @return
     */
    public int findLength(int[] A, int[] B) {
        if (A == null || B == null) {
            return 0;
        }

        int maxLen = 0;
        int[][] length = new int[A.length][B.length];
        for (int i = 0; i < A.length; i++) {
            length[i][0] = (A[i] == B[0] ? 1 : 0);
            maxLen = Math.max(length[i][0], maxLen);
        }

        for (int i = 0; i < B.length; i++) {
            length[0][i] = (A[0] == B[i] ? 1 : 0);
            maxLen = Math.max(length[0][i], maxLen);
        }

        for (int i = 1; i < A.length; i++) {
            for (int j = 1; j < B.length; j++) {
                length[i][j] = (A[i] == B[j] ? length[i - 1][j - 1] + 1 : 0);
                maxLen = Math.max(length[i][j], maxLen);
            }
        }

        return maxLen;
    }

    public boolean isValidSudoku(char[][] board) {
        boolean[] checkArray = new boolean[10];
        int index = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                index = board[i][j] - '0';
                if (index > 9 || index < 1) {
                    continue;
                }
                if (checkArray[index]) {
                    return false;
                } else {
                    checkArray[index] = true;
                }
            }

            checkArray = new boolean[10];
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                index = board[j][i] - '0';
                if (index > 9 || index < 1) {
                    continue;
                }
                if (checkArray[index]) {
                    return false;
                } else {
                    checkArray[index] = true;
                }
            }

            checkArray = new boolean[10];
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = i * 3; k < (i + 1) * 3; k++) {
                    for (int l = j * 3; l < (j + 1) * 3; l++) {
                        index = board[k][l] - '0';
                        if (index > 9 || index < 1) {
                            continue;
                        }
                        if (checkArray[index]) {
                            return false;
                        } else {
                            checkArray[index] = true;
                        }
                    }
                }

                checkArray = new boolean[10];
            }
        }

        return true;
    }

    class Pair {
        int key;
        int value;

        Pair(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public int[] smallestRange(List<List<Integer>> nums) {
        if (nums == null || nums.size() <= 0) {
            return new int[0];
        }

        // 大小为k的小根堆
        int k = nums.size();
        PriorityQueue<Pair> minHeap = new PriorityQueue<>(k, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.key > o2.key ? 1 : o1.key == o2.key ? 0 : -1;
            }
        });

        int[] result = new int[]{0, Integer.MAX_VALUE};
        int maxValue = Integer.MIN_VALUE; // 记录小根堆中的最大数
        int[] moveIndex = new int[k]; // 记录每个List当前下标

        // 初始化堆的初始数据
        List<Integer> iterList = null;
        for (int i = 0; i < nums.size(); i++) {
            iterList = nums.get(i);
            if (iterList == null || iterList.size() <= 0) {
                return new int[0];
            }

            maxValue = Math.max(maxValue, iterList.get(moveIndex[i])); // 记录最大数据
            minHeap.add(new Pair(iterList.get(moveIndex[i]), i)); // 往小根堆里面放，并且记录当前数据来自哪个List
            moveIndex[i]++; // List遍历下标往后挪一位
        }

        while (true) {
            Pair element = minHeap.peek();
            int listIndex = element.value;
            iterList = nums.get(listIndex);

            if ((maxValue - element.key) < (result[1] - result[0])) {
                result[1] = maxValue;
                result[0] = element.key;
            }

            if (moveIndex[listIndex] >= iterList.size()) {
                return result;
            } else {
                maxValue = Math.max(maxValue, iterList.get(moveIndex[listIndex]));
                minHeap.poll();
                minHeap.add(new Pair(iterList.get(moveIndex[listIndex]), listIndex));
                moveIndex[listIndex]++;
            }
        }
    }

    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (nums == null || nums.length <= 0) {
            return 0;
        }

        int result = 0;
        int currentProduct = 1;
        int currentHead = 0;
        for (int i = 0; i < nums.length; i++) {
            currentProduct *= nums[i];
            while (currentProduct >= k) {
                currentProduct /= nums[currentHead];
                currentHead++;
            }

            result += (i - currentHead + 1);
        }

        return result;
    }

    public int subarraySum(int[] nums, int k) {
        if (nums == null || nums.length <= 0) {
            return 0;
        }

        int count = 0;
        int sum = 0;

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1); // initial value sum = 0, occurrence = 1 for case sum = k, k - k = 0 counts

        for (int i = 0; i < nums.length; i++) // iterate nums array
        {
            sum += nums[i]; // update sum
            if (map.containsKey(sum - k)) // if map has sum - k, meaning this new sum - old sum = k
                count += map.get(sum - k); // previous sum might appear more than once
            // this is why we need to add its value
            map.put(sum, map.getOrDefault(sum, 0) + 1); // save new sum into map
        }

        return count;
    }

    public int maxProduct(int[] nums) {
        if (nums == null || nums.length <= 0) {
            return 0;
        }

        int maxProduct = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int currentProduct = 1;
            for (int j = i; j < nums.length; j++) {
                currentProduct *= nums[j];
                maxProduct = Math.max(maxProduct, currentProduct);
            }
        }

        return maxProduct;
    }

    public int maximumProduct(int[] nums) {
        if (nums == null || nums.length <= 0) {
            return 0;
        }

        Arrays.sort(nums);

        int n = nums.length;
        return Math.max(nums[n - 1] * nums[n - 2] * nums[n - 3], nums[0] * nums[1] * nums[n - 1]);
    }

    public int[] productExceptSelf(int[] nums) {
        if (nums == null || nums.length <= 0) {
            return new int[0];
        }

        int[] result = new int[nums.length];
        int currentProduct = 1;
        for (int i = 0; i < nums.length; i++) {
            currentProduct *= nums[i];
            result[i] = currentProduct;
        }

        currentProduct = 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            currentProduct *= nums[i];
            nums[i] = currentProduct;
            if (i == nums.length - 1) {
                result[i] = result[i - 1];
            } else if (i == 0) {
                result[i] = nums[i + 1];
            } else {
                result[i] = result[i - 1] * nums[i + 1];
            }
        }

        return result;
    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;
            nums[index] = nums[index] > 0 ? -nums[index] : nums[index];
        }

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                result.add(i + 1);
            }
        }

        return result;
    }

    public int rob(int[] nums) {
        if (nums == null || nums.length <= 0) { return 0; }
        if (nums.length == 1) { return nums[0]; }
        if (nums.length == 2) { return Math.max(nums[0], nums[1]); }

        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }

        return dp[nums.length - 1];
    }

    public List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length <= 1) {
            return result;
        }

        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;
            nums[index] = -1 * nums[index];
            if (nums[index] > 0) {
                result.add(index + 1);
            }
        }

        return result;
    }

    public boolean checkSubarraySum(int[] nums, int k) {
        for (int i = 0; i < nums.length; ++i) {
            int sum = nums[i];
            for (int j = i + 1; j < nums.length; ++j) {
                sum += nums[j];
                if (sum == k) return true;
                if (k != 0 && sum % k == 0) return true;
            }
        }

        return false;
    }

    /**
     * Google Dynamic programming
     * https://juejin.im/post/5994df21518825243c3a4a20
     * @param num
     * @return
     */
    public int findIntegers(int num) {
        if (num <= 1) {
            return num+1;
        }

        String numStr = Integer.toBinaryString(num);
        int[] intermediateResult = new int[numStr.length()];

        intermediateResult[0] = 1;
        intermediateResult[1] = 2;
        for (int i = 2; i < numStr.length(); i++) {
            intermediateResult[i] = intermediateResult[i-1] + intermediateResult[i-2];
        }

        int result = 0;
        for (int i = numStr.length() - 1; i >= 0; i++) {
            if (numStr.charAt(i) == '1') {
                result += intermediateResult[i];
            }
        }

        return result;
    }

    /**
     * 二叉树的最大路径和
     * https://www.cnblogs.com/junliu37/p/7224172.html
     * @param root
     * @return
     */
    private int max = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        helper(root);
        return max;
    }

    public int helper(TreeNode root) {
        if(root == null) return 0;
        int left = helper(root.left);
        int right = helper(root.right);
        //连接父节点的最大路径是一、二、四这三种情况的最大值
        int currSum = Math.max(Math.max(left + root.val, right + root.val), root.val);
        //当前节点的最大路径是一、二、三、四这四种情况的最大值
        int currMax = Math.max(currSum, left + right + root.val);
        //用当前最大来更新全局最大
        max = Math.max(currMax, max);
        return currSum;
    }

    /**
     * 路径总和
     *
     * @param root
     * @param sum
     * @return
     */
    boolean hasSum = false;
    public boolean hasPathSum(TreeNode root, int sum) {
        iterHelper(root, 0, sum);
        return hasSum;
    }

    private void iterHelper(TreeNode root, int currentSum, int sum) {
        if (root == null) {
            return;
        }

        currentSum += root.val;
        if (currentSum == sum && root.left == null && root.right == null) {
            hasSum = true;
        }

        iterHelper(root.left, currentSum, sum);
        iterHelper(root.right, currentSum, sum);
    }

    /**
     * 最长和谐子数组
     *
     * @param nums
     * @return
     */
    public int findLHS(int[] nums) {;;
        if (nums == null || nums.length <= 1) {
            return 0;
        }

        Map<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.putIfAbsent(nums[i], 1) != null) {
                map.put(nums[i], map.get(nums[i]) + 1);
            }
        }

        int index = 0;
        Map.Entry<Integer, Integer>[] entrys = new Map.Entry[map.size()];
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            entrys[index++] = entry;
        }

        if (entrys.length <= 1) {
            return 0;
        }

        int result = 0;
        for (int i = 0; i < entrys.length - 1; i++) {
            if ((entrys[i + 1].getKey() - entrys[i].getKey()) == 1) {
                result = Math.max(result, entrys[i + 1].getValue() + entrys[i].getValue());
            }
        }

        return result;
    }

    public double largestSumOfAverages(int[] A, int K) {

        return 0.0D;
    }

    /**
     *
     * @param nums
     * @return
     */
    public List<Integer> largestDivisibleSubset(int[] nums) {

        return null;
    }

    /**
     * 每次能爬一层或两层
     * @param n
     * @return
     */
    public int climbStairs(int n) {
        if (n == 1) { return 1; }
        if (n == 2) { return 2; }

        int[] max = new int[n];

        max[0] = 1;
        max[1] = 2;

        for (int i = 2; i < n; i++) {
            max[i] = max[i - 2] + max[i - 1];
        }

        return max[n - 1];
    }

    public int minCostClimbingStairs(int[] cost) {
        if (cost == null || cost.length <= 0) { return 0; }

        int length = cost.length;
        if (length == 1) { return cost[0]; }
        if (length == 2) { return Math.min(cost[0], cost[1]); }

        int[] min = new int[length];
        min[0] = cost[0];
        min[1] = cost[1];
        min[2] = Math.min(cost[0] + cost[2], cost[1] + cost[2]);

        for (int i = 3; i < length; i++) {
            min[i] = Math.min(min[i-1] + cost[i], min[i-2] + cost[i]);
        }

        return Math.min(min[length - 2], min[length - 1]);
    }

    public int uniquePaths(int m, int n) {
        if (m <= 1 || n <= 1) {
            return 1;
        }

        int[][] grid = new int[n][m];

        for (int i = 0; i < n; i++) {
            grid[i][0] = 1;
        }

        for (int i = 0; i < m; i++) {
            grid[0][i] = 1;
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                grid[i][j] = grid[i - 1][j] + grid[i][j - 1];
            }
        }

        return grid[n-1][m-1];
    }

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        if (obstacleGrid == null) {
            return 0;
        }

        int horizontallength = obstacleGrid[0].length;
        int verticallength = obstacleGrid.length;

        obstacleGrid[0][0] = (obstacleGrid[0][0] == 1 ? 0 : 1);
        for (int i = 1; i < horizontallength; i++) {
            if (obstacleGrid[0][i] == 1) {
                obstacleGrid[0][i] = 0;
            } else {
                obstacleGrid[0][i] = obstacleGrid[0][i - 1];
            }
        }

        for (int i = 1; i < verticallength; i++) {
            if (obstacleGrid[i][0] == 1) {
                obstacleGrid[i][0] = 0;
            } else {
                obstacleGrid[i][0] = obstacleGrid[i - 1][0];
            }
        }

        for (int i = 1; i < verticallength; i++) {
            for (int j = 1; j < horizontallength; j++) {
                if (obstacleGrid[i][j] == 1) {
                    obstacleGrid[i][j] = 0;
                } else {
                    obstacleGrid[i][j] = obstacleGrid[i - 1][j] + obstacleGrid[i][j - 1];
                }
            }
        }

        return obstacleGrid[verticallength - 1][horizontallength - 1];
    }

    public int minPathSum(int[][] grid) {
        if (grid == null) {
            return 0;
        }

        int horizontallength = grid[0].length;
        int verticallength = grid.length;

        for (int i = 1; i < verticallength; i++) { grid[i][0] += grid[i - 1][0]; }
        for (int i = 1; i < horizontallength; i++) { grid[0][i] += grid[0][i - 1]; }

        for (int i = 1; i < verticallength; i++) {
            for (int j = 1; j < horizontallength; j++) {
                grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
            }
        }

        return grid[verticallength - 1][horizontallength - 1];
    }

    public int coinChange(int[] coins, int amount) {
        if (coins == null || amount <= 0) { return  -1; }

        Arrays.sort(coins);

        int maxInt = Integer.MAX_VALUE;
        int[] dp = new int[amount + 1];
        for (int i = 0; i <= amount; i++) { dp[i] = maxInt; }

        for (int coin : coins) { if (coin <= amount) {dp[coin] = 1;} }
        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (i - coins[j] > 0 && dp[i - coins[j]] != maxInt) { dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1); }
            }
        }

        return dp[amount] == maxInt ? -1 : dp[amount];
    }

    public int calculateMinimumHP(int[][] dungeon) {
        if (dungeon == null) { return 1; }

        int verticalLength = dungeon.length;
        int horizontalLength = dungeon[0].length;
        int[][] minMatrix = new int[verticalLength][horizontalLength];

        for (int i = 1; i < horizontalLength; i++) {
            dungeon[0][i] += dungeon[0][i - 1];
            dungeon[0][i] += dungeon[0][i - 1];
        }
        for (int i = 1; i < verticalLength; i++) {
            dungeon[i][0] += dungeon[i - 1][0];
            dungeon[i][0] += dungeon[i - 1][0];
        }

        for (int i = 0; i < verticalLength; i++) {
            for (int j = 0; j < horizontalLength; j++) {
                dungeon[i][j] += Math.min(dungeon[i - 1][j], dungeon[i][j - 1]);
            }
        }

        return Math.abs(dungeon[verticalLength - 1][horizontalLength - 1]) + 1;
    }
}
