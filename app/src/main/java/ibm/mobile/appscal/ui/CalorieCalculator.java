package ibm.mobile.appscal.ui;

public class CalorieCalculator {

    public static int calculateBMR(float weight, float height, int age, String gender) {
        float bmr;
        if (gender.equalsIgnoreCase("pria")) {
            bmr = 10 * weight + 6.25f * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25f * height - 5 * age - 161;
        }
        return Math.round(bmr * 1.2f);
    }

    public static float calculateTDEE(float bmr, float activityFactor) {
        return bmr * activityFactor;
    }

    public static float calculatePercentage(float part, float total) {
        return (part / total) * 100;
    }
}
