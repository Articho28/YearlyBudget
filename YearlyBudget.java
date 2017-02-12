
/**
 *
 * @author Artsiom Skliar
 */
import java.util.Arrays; 
import java.text.DecimalFormat; //important to format the outputs to the first decimal 
public class YearlyBudget {

  
  //This is a class made to keep track of my yearly expenses. Each method is described below. 
  
   //Main method
    public static void main(String[] args) { 
        DecimalFormat df = new DecimalFormat("#0.0"); //method that will round the value to the first decimal and then convert it to a string. 
        //first, declare are relevant variables and parse them so they can be used as their proper types
        double pretaxIncome = Double.parseDouble(args[0]);
        double creditBalance = Double.parseDouble(args[1]);
        double interestRate = Double.parseDouble(args[2]);
        long creditNumber = Long.parseLong(args[3]);
        double monthlyRent = Double.parseDouble(args[4]);
        if (!validateCreditCard(creditNumber)) { //checks if the credit card number is valid
            System.out.println("Invalid card.");
        } else {
          //declare the arrays
            double[] expensesArray = new double[12];
            double[] paymentsArray = new double[12];
            double aftertaxIncome = pretaxIncome - calculateTax(pretaxIncome, 10000, 20, 20000, 30, 45000, 50.0); //the tax brackets shown here are arbitrary.
            expensesArray = buildExpenses(monthlyRent);
            paymentsArray = buildPayments(aftertaxIncome);
            printBalance(creditBalance, interestRate, expensesArray, paymentsArray); // print the credit card balance and calculate the interest relative to payments and expenses
            double totalSavings = 0.0;
            for (int i = 0; i < 12; i++) {
                double monthlySavings = monthlySavings(expensesArray[i], aftertaxIncome / 12 - paymentsArray[i]);//monthly expenses - montlhy income 
                System.out.println("Savings for Month " + (i + 1) + " " + df.format(monthlySavings)); //monthlySavings are formatted to the first decimal and returned as string 
                totalSavings += monthlySavings;
            }
            System.out.println("Total year savings : " + df.format(totalSavings));//totalSavings formatted 

    /*The following were inputs used to test each method individually
            System.out.println(calculateTax(100000,10000, 20, 20000, 30, 45000, 50.0));
            System.out.println(monthlySavings(25000,30000));
            System.out.println(validateCreditCard(5191230180373889L));
            double expenses[] = {100.0,200.0,300.0,400.0,500.0,600.0,700.0,800.0,900.0,1000.0,1100.0,1200};
            double payments[] = {100.0,200.0,300.0,400.0,500.0,600.0,700.0,800.0,900.0,1000.0,1100.0,1200}; 
            printBalance(100.0, 20.0, expenses, payments);
            buildExpenses(500);
            buildPayments(50000);*/
        }
    }

    //This method serves to calculate one's taxes depending on their income. The cost of taxes will depend on the tax bracket to which the individual belongs. 
    //The tax brackets are to be input as arguments. 
    public static double calculateTax(double yearlyIncome, double bracket1Dollars, double bracket1Rate, double bracket2Dollars, double bracket2Rate, double bracket3Dollars, double bracket3Rate) {
        double totalTaxes = 0.0; //Declare the variable before manipulations.
        if (yearlyIncome >= bracket2Dollars) { //if the income is greater than the second bracket, calculculate the tax for all the money between bracket 1 and 2
            totalTaxes = (bracket2Dollars - bracket1Dollars) * bracket1Rate / 100;
            if (yearlyIncome >= bracket3Dollars) { //if the income is greater than bracket 3, add to the already calculated tax the money above bracket 3 and between brackets 2 and 3. 
                totalTaxes += ((bracket3Dollars - bracket2Dollars) * bracket2Rate + (yearlyIncome - bracket3Dollars) * bracket3Rate) / 100;
            } else { //if the income is less than bracket 3, than only add the money between bracket 2 and 3 to the previously calculated tax.
                totalTaxes += (yearlyIncome - bracket2Dollars) * bracket2Rate / 100;
            }
        } else if (yearlyIncome >= bracket1Dollars) { //if the income is less than bracket 2, only calculate the tax betwee brackets 1 and 2.
            totalTaxes = (yearlyIncome - bracket1Dollars) * bracket1Rate / 100;
            //if income is less than bracket 1, it will return the first declared value: 0.0.
        }
        return totalTaxes;
    }

    //This is a simple method to keep track of monthtly savings by substracting expenses to income. 
    public static double monthlySavings(double monthlyExpenses, double monthlyIncome) { //substract expenses to income. 
        return monthlyIncome - monthlyExpenses;
    }

    //This is a checksum algorithm to verify if a provided number is a valid credit card number. It follows the basis of Luhn's algorithm. 
    public static boolean validateCreditCard(long creditNumber) {
        long lastDigit; //variable to hold intermidiate extracted last digit
        long evenSum = 0; //variable to accumulate even digit sum
        long oddCalc = 0; ////variable to accumulate odd digit sum
        for (int i = 0; i <= 15; i++) {//<=15 because there are 16 digits in a credit card
            if (creditNumber == 0) {
                return false; // if input contains less than 16 digits number
            }
            lastDigit = creditNumber % 10;//extracting last digit
            //System.out.println("When the last digit is:" + lastDigit);
            creditNumber = (creditNumber - lastDigit) / 10;// changing pointer to the next digit
            if (i % 2 == 0) {       //checking if the digit is at an even location 
                evenSum += lastDigit; // add even digit to intermidiate sum 
            } else { // if the digit is located at an odd location odd
                oddCalc += (lastDigit * 2) % 9;//apply formula for odd digits
                if (lastDigit == 9) {
                    /*The Luhn algorithm states that if the double of the last digit is greater than 10 (e.g. 6*2 = 12), you must subsract 9 to it (e.g. 12-9=3) before adding it to the sum of numbers at
                     * odd locations. 
                     * This is the same as taking modulo 9 of the product for most cases (e.g. 12%9 = 3). However, if the last  digit is 9, then Luhn algorithm says that we should do (2*9)-9=9. 
                     * But (2*9)%9=0, which is incorrect according to Luhn's algorithm. As a result, any credit card carrying a 9 at an odd location will be considered invalid. 
                     * (source: Wikipedia). 
                     */
                    oddCalc += 9;
                }
            }
        }
        //System.out.println(evenSum); Used to test my program as I went along
        //System.out.println(oddCalc); Used to test my program as I went along
        //System.out.println(evenSum + oddCalc);  Used to test my program as I went along 
        return ((evenSum + oddCalc) % 10 == 0 && creditNumber == 0);//check if multiple of 10 and if all digits were treated
    }

   //This method puts together my yearly expenses
    public static double[] buildExpenses(double monthlyRent) {
        double[] monthlyExpenses = new double[12];
        for (int i = 0; i < monthlyExpenses.length; i++) { // i=0 represents January, i=1 represents February, ..., i=11 represents December.  
            monthlyExpenses[i] = monthlyRent + 600; //Every month, the minimum cost of living is rent and commodities (food, clothes, etc.)
            if (i == 0 || i == 5) {// in January and June, add an extra 200$ for going to the dentist 
                monthlyExpenses[i] += 200;
            } else if (i == 3 || i == 6) {// in April and July, extra 100$ spent on family gifts.
                monthlyExpenses[i] += 100;
            } else if (i == 8) { // in September, 300$ on textbooks and 100$ on family gifts.
                monthlyExpenses[i] += (100 + 300);
            } else if (i == 11) {// in December, 200$ extra for holidays.
                monthlyExpenses[i] += 200;
            }
        }
        return monthlyExpenses;
    }

    //This method keeps track of the payements I made. 
    public static double[] buildPayments(double yearlyIncome) {
        double[] monthlyCreditBill = new double[12];
        for (int i = 0; i < monthlyCreditBill.length; i++) {
            monthlyCreditBill[i] = (yearlyIncome / 12) * 0.1; //each month, the bill is 10% of your monthly income
            if (i == 8) {
                monthlyCreditBill[i] += 200; //In September, an extra 200$ is added to the credit bill
            }
            if (i == 11) {
                monthlyCreditBill[i] += 150; //In December, an extra 150$ is added to the credit bill
            } 
        }
        return monthlyCreditBill;
    }

    //This method prints my final balance. 
    public static void printBalance(double creditBalance, double interestRate, double expenses[], double payments[]) { //declare the inputs
        DecimalFormat df = new DecimalFormat("#0.0"); //method that will round to the first decimal and then convert to a string. It matches the format given in the assignment instructions. 
        for (int i = 0; i <= 11; i++) {
            creditBalance += expenses[i] - payments[i];// credit balance is the difference between the expenses nad the payments of each month.
            if (creditBalance > 0) {//if the credit balance is positive, charge interest.
                creditBalance *= (100 + interestRate / 12) / 100;
            }
            System.out.println("Month " + (i + 1) + " balance " + df.format(creditBalance)); // print the balance for each month
        }
    }
}
