package LeetCode;

class NumArray {
	private int[] left2RightSum;
	private int[] right2LeftSum;
	private int[] nums;
	private int numLength;

	public NumArray(int[] nums) {
		if (nums == null || nums.length <= 0) {
			return;
		}

		this.nums = nums;
		numLength = nums.length;
		left2RightSum = new int[numLength];
		right2LeftSum = new int[numLength];

		left2RightSum[0] = nums[0];
		for (int i = 1; i < numLength; i++) {
			left2RightSum[i] = left2RightSum[i - 1] + nums[i];
		}

		right2LeftSum[numLength - 1] = nums[numLength - 1];
		for (int i = numLength - 2; i >= 0 ; i--) {
			right2LeftSum[i] = right2LeftSum[i + 1] + nums[i];
		}
	}

	public int sumRange(int i, int j) {
		if (j < i || j > numLength - 1 || i < 0) {
			return 0;
		}

		if (i == 0) { return left2RightSum[j]; }
		if (j == numLength - 1) { return right2LeftSum[i]; }

		return right2LeftSum[0] - left2RightSum[i] - right2LeftSum[j] + nums[i] + nums[j];
	}
}
