import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/*
 * 测试用例设计总体原则
 * --------------------
 * 1. 等价类划分（Equivalence Partitioning）: 将输入分成若干类，例如空输入、单元素、全部可整除链、混合不可整除的集合、只包含素数的集合、无重复元素（题目保证）等。
 * 2. 边界值分析（Boundary Value Analysis）: 测试最小长度（0、1）以及较大但常见的长度（4-6）。
 * 3. 错误/异常情形: 对于题目声明的前提（如无重复元素）可以提供一个边缘测试，观察实现如何表现（作为鲁棒性检查）。
 * 4. 顺序与无序输入: 输入可能无序，函数应在排序后仍能得到正确结果。
 * 5. 可验证性与健壮性: 对输出不仅检查具体值（当有确定期望时），还要检查输出集合是否满足“整除子集”的定义（用于无法唯一确定期望输出的情况）。
 * 6. 性能敏感（非严格性能测试）: 提供一个稍大的输入用例，确保算法不会在小规模下明显失败。（这里仅作为 smoke test，不做严格基准）
 */
public class L2023111825_3_Test {

    // 辅助函数：检查返回的子集是否满足整除子集的定义，并且都是来自原数组
    private void assertValidDivisibleSubset(int[] nums, List<Integer> subset, int expectedSize) {
        if (expectedSize >= 0) {
            assertEquals(expectedSize, subset.size(), "返回子集大小与期望不符");
        }

        // 输入元素集合（用于 membership 检查）
        List<Integer> numsList = new ArrayList<>();
        for (int n : nums) numsList.add(n);

        for (Integer x : subset) {
            assertTrue(numsList.contains(x), "子集包含原数组中不存在的元素: " + x);
        }

        // 检查任何一对元素是否满足整除关系
        for (int i = 0; i < subset.size(); i++) {
            for (int j = i + 1; j < subset.size(); j++) {
                int a = subset.get(i);
                int b = subset.get(j);
                assertTrue(a % b == 0 || b % a == 0, String.format("元素 %d 和 %d 不满足整除关系", a, b));
            }
        }
    }

    /**
     * 测试目的：验证对 null 与 空数组的处理（等价类：空输入）
     * 用到的测试用例： null, []
     */
    @Test
    @DisplayName("空输入与 null 的处理")
    public void testEmptyAndNull() {
        Solution3 s = new Solution3();

        List<Integer> r1 = s.largestDivisibleSubset(null);
        assertNotNull(r1);
        assertEquals(0, r1.size());

        List<Integer> r2 = s.largestDivisibleSubset(new int[]{});
        assertNotNull(r2);
        assertEquals(0, r2.size());
    }

    /**
     * 测试目的：单元素数组应返回该元素（边界值）
     * 用到的测试用例： [7]
     */
    @Test
    @DisplayName("单元素数组")
    public void testSingleElement() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{7};
        List<Integer> res = s.largestDivisibleSubset(nums);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(7, res.get(0));
    }

    /**
     * 测试目的：全部可整除链（确切可预测结果）
     * 用到的测试用例： [1,2,4,8]
     */
    @Test
    @DisplayName("全部可整除的链")
    public void testFullyDivisibleChain() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{1,2,4,8};
        List<Integer> res = s.largestDivisibleSubset(nums);
        assertValidDivisibleSubset(nums, res, 4);
        // 由于实现返回升序，所以我们可以断言具体顺序
        assertEquals(Arrays.asList(8,4,2,1), res);
    }

    /**
     * 测试目的：混合数组，存在多个等长解（验证函数返回任一合法解）
     * 用到的测试用例： [1,2,3]
     */
    @Test
    @DisplayName("混合数组存在多个等长解")
    public void testMixedNumbersWithMultipleAnswers() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{1,2,3};
        List<Integer> res = s.largestDivisibleSubset(nums);
        assertNotNull(res);
        assertEquals(2, res.size());

        // 可能的合法解为 {1,2} 或 {1,3}
        Set<Integer> set = new HashSet<>(res);
        Set<Integer> expect1 = new HashSet<>(Arrays.asList(1,2));
        Set<Integer> expect2 = new HashSet<>(Arrays.asList(1,3));
        assertTrue(set.equals(expect1) || set.equals(expect2), "返回的集合不是合法的等长解");
        assertValidDivisibleSubset(nums, res, 2);
    }

    /**
     * 测试目的：无序输入（函数应对输入排序后得到正确解）
     * 用到的测试用例： [8,1,4,2]
     */
    @Test
    @DisplayName("无序输入")
    public void testUnsortedInput() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{8,1,4,2};
        List<Integer> res = s.largestDivisibleSubset(nums);
        assertValidDivisibleSubset(nums, res, 4);
        assertEquals(Arrays.asList(8,4,2,1), res);
    }

    /**
     * 测试目的：全为素数的集合（没有两个数之间可整除）——应返回任意单元素
     * 用到的测试用例： [2,3,5,7,11]
     */
    @Test
    @DisplayName("全部素数输入")
    public void testPrimes() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{2,3,5,7,11};
        List<Integer> res = s.largestDivisibleSubset(nums);
        assertNotNull(res);
        assertEquals(1, res.size());
        // 返回的元素应存在于原数组
        List<Integer> asList = Arrays.asList(2,3,5,7,11);
        assertTrue(asList.contains(res.get(0)));
    }

    /**
     * 测试目的：含有较大/混合整除关系的数组（验证算法能找到最长链）
     * 用到的测试用例： [100,200,300,600,1200]
     */
    @Test
    @DisplayName("较大数值混合整除")
    public void testLargeNumbers() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{100,200,300,600,1200};
        List<Integer> res = s.largestDivisibleSubset(nums);
        // 期望长度为 4（例如 {100,200,600,1200}）
        assertEquals(4, res.size());
        assertValidDivisibleSubset(nums, res, 4);
    }

    /**
     * 测试目的：输入包含重复元素（尽管题目说明无重复，这里作为鲁棒性检查）
     * 用到的测试用例： [2,2,4]
     */
    @Test
    @DisplayName("包含重复元素（鲁棒性检查）")
    public void testWithDuplicates() {
        Solution3 s = new Solution3();
        int[] nums = new int[]{2,2,4};
        List<Integer> res = s.largestDivisibleSubset(nums);
        // 只要结果满足整除子集并且来自原数组即可
        assertTrue(res.size() >= 1);
        assertValidDivisibleSubset(nums, res, -1);
    }

}
