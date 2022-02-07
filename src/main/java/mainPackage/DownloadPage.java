package mainPackage;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DownloadPage {

	WebDriver driver = null;

	public DownloadPage(WebDriver driver) {
		this.driver = driver;
	}

	public void selectSaleyear(String syr) {
		String saleYearxpath = "//td[./span[contains(text(),'Sale Year')]]/following-sibling::td/select";
		Select saleYear = new Select(driver.findElement(By.xpath(saleYearxpath)));
		saleYear.selectByVisibleText(syr.trim());
	}

	public void selectSaleNo(String sno) {
		String saleNoxpath = "//td[./span[contains(text(),'Sale No')]]/following-sibling::td/select";
		Select saleNo = new Select(driver.findElement(By.xpath(saleNoxpath)));
		saleNo.selectByVisibleText(sno.trim());
	}

	public void selectIssuer() {
		String issuerXpath = "//input[@value='Issuer']";
		driver.findElement(By.xpath(issuerXpath)).click();
		;
	}

	public void selectGSTIRN() {
		String GSTIRNXpath = "//input[@value='GST IRN']";
		driver.findElement(By.xpath(GSTIRNXpath)).click();
		;
	}

	public void selectAuctionCenter(String ac) {
		String acxpath = "//td[./span[contains(text(),'Auction Center')]]/following-sibling::td[1]/select";
		Select aucCen = new Select(driver.findElement(By.xpath(acxpath)));
		aucCen.selectByVisibleText(ac.trim());
	}

	public void clickDownload() {

		String downloadXpath = "//input[@value='Download']";
		driver.findElement(By.xpath(downloadXpath)).click();
	}

	public void clickExportIRN() {

		String ExportIRNXpath = "//input[@value='Export IRN Details']";
		driver.findElement(By.xpath(ExportIRNXpath)).click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public boolean isDataPresent() {
		String noDataXpath = "//td[contains(text(),'No Data Found.')]";
		try {
			driver.findElement(By.xpath(noDataXpath));
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public void clickRefresh() {
		String refreshXpath = "//input[@value='Refresh']";
		driver.findElement(By.xpath(refreshXpath)).click();
	}

	public void clickSelectAll() {
		String selectAllXpath = "//input[./following-sibling::label[contains(text(),'Select All')]]";
		driver.findElement(By.xpath(selectAllXpath)).click();
	}

	public void chooseFile(String file) {
		System.out.println("file uploading =============="+file);
		String chooseFileXpath = "//input[@type='file']";
		driver.findElement(By.xpath(chooseFileXpath)).sendKeys(file);
	}

	public boolean clickUpload() {
		String uploadXpath = "//input[@value='Upload']";
		driver.findElement(By.xpath(uploadXpath)).click();
		try {
			handleAlert();
			System.out.println("File upload fail");
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean clickIRNUpload() {
		String IRNuploadXpath = "//input[@value='Upload IRN Details']";
		driver.findElement(By.xpath(IRNuploadXpath)).click();
		try {
			handleAlert();
			System.out.println("File upload fail");
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public void handleAlert() {
		driver.switchTo().alert().accept();
	}
	
	public void uploadExcelFile(String uploadfilePath)
	{
		File directoryPath = new File(uploadfilePath);
		String fileNames[] = directoryPath.list();
		
		String tableXpath = "//table[@id='MainContent_GridView1']//tr";
		List<WebElement> tableRow = driver.findElements(By.xpath(tableXpath));
		for(int i = 2; i<tableRow.size(); i++)
		{
			String checkBoxXpath = tableXpath + "["+ i + "]/td[1]";
			driver.findElement(By.xpath(checkBoxXpath)).click();
			
			String invoiceNoxpath = tableXpath + "["+ i + "]/td[7]";
			String invoiceNo = driver.findElement(By.xpath(invoiceNoxpath)).getText();
			
			for(String fileName:fileNames)
			{
				if(fileName.startsWith(invoiceNo))
				{
					chooseFile(uploadfilePath + "\\" + fileName);
					clickIRNUpload();
					driver.findElement(By.xpath(checkBoxXpath)).click();
					break;
				}
			}
			
		}
	}
	
	public void uploadPDFFile(String uploadfilePath)
	{
		File directoryPath = new File(uploadfilePath);
		String fileNames[] = directoryPath.list();
		
		String tableXpath = "//table[@id='MainContent_GridSellerDownload']//tr";
		List<WebElement> tableRow = driver.findElements(By.xpath(tableXpath));
		for(int i = 2; i<tableRow.size(); i++)
		{
			String checkBoxXpath = tableXpath + "["+ i + "]/td[1]/input";
			driver.findElement(By.xpath(checkBoxXpath)).click();
			
			String invoiceNoxpath = tableXpath + "["+ i + "]/td[10]";
			String invoiceNo = driver.findElement(By.xpath(invoiceNoxpath)).getText();
			
			for(String fileName:fileNames)
			{
				if(fileName.startsWith(invoiceNo))
				{
					chooseFile(uploadfilePath + "\\" + fileName);
					clickUpload();
					driver.findElement(By.xpath(checkBoxXpath)).click();
					break;
				}
			}
			
		}
	}

}
