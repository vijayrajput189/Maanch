package SignInScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignIn {

    public static void main(String[] args) throws IOException {
        // Setup WebDriver
        System.setProperty("webdriver.chrome.driver", "C://Vijay//projects//chromedriver-win64/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        
        String username = "demo-user";
        String password = "Demo@321!";
        driver.get("http://" + username + ":" + password + "@demo.maanch.com/");
        driver.findElement(By.linkText("Login/Sign up")).click();

        // Read Excel Data
        File file = new File("C://Vijay//Assignment//TestData.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);

        // Access the "SignUpTestData" sheet
        XSSFSheet sheet = (XSSFSheet) workbook.getSheet("SignInTestData");
        Iterator<Row> rows = sheet.iterator();

        // Skip header row
        rows.next();

        // Iterate through rows and fill the form
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = rows.next();
            
            String email = getCellValue(row, 0);
            String passwordFieldValue = getCellValue(row, 1);
          
            try {
                // Fill the signup form
                WebElement emailField = driver.findElement(By.id("email"));
                WebElement passwordField = driver.findElement(By.id("password"));
                WebElement loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']"));

                // Clear fields and enter data
                emailField.clear();
                passwordField.clear();
                emailField.sendKeys(email);
                passwordField.sendKeys(passwordFieldValue);
                String folderPath = "C:\\Vijay\\Assignment\\Login/Sc05_TC" + String.format("%02d", i);
                // Take Screenshot after filling the form
                takeScreenshot(driver, folderPath + "//credentials_" +"before"  + ".png");

                // Submit the form
                
                loginButton.click();
                takeScreenshot(driver, folderPath + "//credentials_" +"after"  + ".png");
                driver.navigate().refresh();
                

                // Wait for login to process and then perform logout
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(ExpectedConditions.elementToBeClickable(By.id("dropdownMenuLink"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();

                // Refresh the page after logout
               

                // Wait for page to reload and navigate back to login page
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login/Sign up"))).click();
                
                // Take Screenshot after submitting the form
              
            } catch (Exception e) {
                System.out.println("Error during form submission for user: " + email);
                e.printStackTrace();
            }
        }

        // Close resources and browser
        workbook.close();
        inputStream.close();
        driver.quit();
    }

    // Method to handle different cell types and return cell value as String
    public static String getCellValue(Row row, int columnIndex) {
        if (row.getCell(columnIndex) != null) {
            CellType cellType = row.getCell(columnIndex).getCellType();
            if (cellType == CellType.STRING) {
                String cellValue = row.getCell(columnIndex).getStringCellValue();
                // Check if the cell contains the string "blank"
                if (cellValue.equalsIgnoreCase("blank")) {
                    return ""; // Return empty string if the value is "(blank)"
                }
                return cellValue;  // Return the actual string if it's not "(blank)"
            } else if (cellType == CellType.NUMERIC) {
                return String.valueOf(row.getCell(columnIndex).getNumericCellValue());
            } else if (cellType == CellType.BLANK) {
                return ""; // Return empty string for blank cells
            }
        }
        return ""; // Return empty string if the cell is null
    }
    

    // Method to take a screenshot
    private static void takeScreenshot(WebDriver driver, String filePath) throws IOException {
        File screenshotDir = new File(filePath).getParentFile();
        
        // Check if directory exists, if not, create it
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        TakesScreenshot scrShot = (TakesScreenshot) driver;
        File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File destFile = new File(filePath + ".png");
        FileUtils.copyFile(srcFile, destFile);
    }
}
