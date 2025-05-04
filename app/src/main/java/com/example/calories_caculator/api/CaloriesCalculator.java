package com.example.calories_caculator.api;

import com.example.calories_caculator.model.User;

public class CaloriesCalculator {

    // Hằng số cho các mục tiêu
    public static final String GOAL_BULKING = "bulking";
    public static final String GOAL_CUTTING = "cutting";
    public static final String GOAL_MAINTENANCE = "maintenance";

    // Hằng số cho giới tính
    public static final String GENDER_MALE = "Nam";
    public static final String GENDER_FEMALE = "Nữ";

    /**
     * Tính BMR (Basal Metabolic Rate) - Tỷ lệ trao đổi chất cơ bản
     * Sử dụng công thức Mifflin-St Jeor
     */
    public static double calculateBMR(User user) {
        if (user == null) return 0;

        double bmr = 0;

        // Kiểm tra giới tính để áp dụng công thức phù hợp
        if (GENDER_MALE.equalsIgnoreCase(user.getGender())) {
            // Công thức cho nam: BMR = 10 * cân nặng (kg) + 6.25 * chiều cao (cm) - 5 * tuổi + 5
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else {
            // Công thức cho nữ: BMR = 10 * cân nặng (kg) + 6.25 * chiều cao (cm) - 5 * tuổi - 161
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }

        return bmr;
    }

    /**
     * Tính TDEE (Total Daily Energy Expenditure) - Tổng năng lượng tiêu thụ hàng ngày
     * TDEE = BMR * Hệ số hoạt động
     */
    public static double calculateTDEE(User user) {
        double bmr = calculateBMR(user);
        double activityFactor = getActivityFactor(user.getActivityLevel());

        return bmr * activityFactor;
    }

    /**
     * Lấy hệ số hoạt động dựa trên mức độ hoạt động
     */
    public static double getActivityFactor(String activityLevel) {
        if (activityLevel == null) return 1.2; // Mặc định là ít hoạt động

        switch (activityLevel) {
            case "Ít vận động":
                return 1.2; // Sedentary (little or no exercise)
            case "Nhẹ nhàng":
                return 1.375; // Lightly active (light exercise/sports 1-3 days/week)
            case "Vừa phải":
                return 1.55; // Moderately active (moderate exercise/sports 3-5 days/week)
            case "Năng động":
                return 1.725; // Very active (hard exercise/sports 6-7 days/week)
            case "Rất năng động":
                return 1.9; // Extra active (very hard exercise, physical job or training twice a day)
            default:
                return 1.2;
        }
    }

    /**
     * Tính lượng calories dựa trên mục tiêu của người dùng
     */
    public static double calculateTargetCalories(User user, String goal) {
        double tdee = calculateTDEE(user);

        if (goal == null) {
            return tdee; // Mặc định là duy trì nếu chưa chọn mục tiêu
        }

        switch (goal) {
            case GOAL_BULKING:
                return tdee + 500; // Tăng 500 calories cho mục tiêu tăng cân
            case GOAL_CUTTING:
                return Math.max(1200, tdee - 500); // Giảm 500 calories cho mục tiêu giảm cân, nhưng không dưới 1200
            case GOAL_MAINTENANCE:
            default:
                return tdee; // Giữ nguyên TDEE cho mục tiêu duy trì
        }
    }

    /**
     * Tính phân bổ macros (protein, carbs, fat) dựa trên mục tiêu
     * @return double[] với thứ tự [protein, carbs, fat] tính bằng gram
     */
    public static double[] calculateMacros(User user, String goal) {
        double targetCalories = calculateTargetCalories(user, goal);
        double[] macros = new double[3]; // [protein, carbs, fat]

        if (GOAL_BULKING.equals(goal)) {
            // Tăng cân: 30% protein, 50% carbs, 20% fat
            macros[0] = (targetCalories * 0.3) / 4; // Protein (4 calo/g)
            macros[1] = (targetCalories * 0.5) / 4; // Carbs (4 calo/g)
            macros[2] = (targetCalories * 0.2) / 9; // Fat (9 calo/g)
        } else if (GOAL_CUTTING.equals(goal)) {
            // Giảm cân: 40% protein, 30% carbs, 30% fat
            macros[0] = (targetCalories * 0.4) / 4; // Protein (4 calo/g)
            macros[1] = (targetCalories * 0.3) / 4; // Carbs (4 calo/g)
            macros[2] = (targetCalories * 0.3) / 9; // Fat (9 calo/g)
        } else {
            // Duy trì: 30% protein, 40% carbs, 30% fat
            macros[0] = (targetCalories * 0.3) / 4; // Protein (4 calo/g)
            macros[1] = (targetCalories * 0.4) / 4; // Carbs (4 calo/g)
            macros[2] = (targetCalories * 0.3) / 9; // Fat (9 calo/g)
        }

        return macros;
    }

    /**
     * Tính lượng calories cho ngày tập và ngày nghỉ (calories cycling)
     * @return double[] với thứ tự [caloriesTrainingDay, caloriesRestDay]
     */
    public static double[] calculateCaloriesCycling(User user, String goal) {
        double targetCalories = calculateTargetCalories(user, goal);
        double[] cyclingCalories = new double[2]; // [trainingDay, restDay]

        if (GOAL_BULKING.equals(goal)) {
            // Ngày tập: +10%, Ngày nghỉ: -5%
            cyclingCalories[0] = targetCalories * 1.1;
            cyclingCalories[1] = targetCalories * 0.95;
        } else if (GOAL_CUTTING.equals(goal)) {
            // Ngày tập: +5%, Ngày nghỉ: -10%
            cyclingCalories[0] = targetCalories * 1.05;
            cyclingCalories[1] = targetCalories * 0.9;
        } else {
            // Duy trì: Ngày tập: +5%, Ngày nghỉ: -5%
            cyclingCalories[0] = targetCalories * 1.05;
            cyclingCalories[1] = targetCalories * 0.95;
        }

        return cyclingCalories;
    }
}