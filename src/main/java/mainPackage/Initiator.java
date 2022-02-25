package mainPackage;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;

public class Initiator {

	private static final String LOG_FILE = "log4j.properties";

	public static void main(String[] args) throws Exception {

		Logger logger = Logger.getLogger(Initiator.class);
		Properties properties = new Properties();
		properties.load(new FileInputStream(LOG_FILE));
		PropertyConfigurator.configure(properties);

		Utility objExcelFile = new Utility();
		String filePath = System.getProperty("user.dir") + "\\resources";
		List<Map<String, String>> data = objExcelFile.readExcel(filePath, "testData.xlsx", "data");

		for (Map<String, String> rowData : data) {
			WebDriver driver = Driver.launchDriver();
			String action = rowData.get("Action").trim();
			LoginPage loginPage = new LoginPage(driver);
			loginPage.selectRole(rowData.get("Role"));
			loginPage.selectUserCode(rowData.get("UserCode"));
			logger.info("======User:" + rowData.get("UserCode") + "=========================");
			loginPage.selectPassword(rowData.get("Password"));
			loginPage.selectAuctionCenter(rowData.get("AuctionCenter"));
			loginPage.clickLogin();
			String error = loginPage.isLoginSuccess();
			if (error != null) {
				logger.debug(error);
				Driver.driverTearDown(driver);
				continue;
			}
			logger.debug("login successful");
			HomePage homePage = new HomePage(driver);
			homePage.clickPublicIssuer(driver);

			DownloadPage downloadPage = new DownloadPage(driver);
			downloadPage.selectSaleyear(rowData.get("SaleYear"));
			downloadPage.selectSaleNo(rowData.get("SaleNo"));
			if (action.contains("PDF")) {
				downloadPage.selectIssuer();
			} else if (action.contains("Excel")) {
				downloadPage.selectGSTIRN();
			}

			int aucIndex = 1;
			while (rowData.get("Auc" + aucIndex) != null) {
				String aucValue = rowData.get("Auc" + aucIndex);
				downloadPage.selectAuctionCenter(aucValue);
				downloadPage.clickRefresh();
				if (action.equals("Upload PDF")) {
					logger.info("Start uploading PDF");
					String uploadfilePath = System.getProperty("user.dir") + "\\PDFUpload";
					downloadPage.uploadPDFFile(uploadfilePath);
					Thread.sleep(1000);
					logger.info("Uploading PDF completed");
					break;
				} else if (action.equals("Upload Excel")) {
					logger.info("Start uploading Excel");
					String uploadfilePath = System.getProperty("user.dir") + "\\ExcelUpload";
					downloadPage.uploadExcelFile(uploadfilePath);
					Thread.sleep(1000);
					logger.info("Uploading Excel completed");
					break;
				}
				if (downloadPage.isDataPresent()) {
					downloadPage.clickSelectAll();
					if (action.equals("Download PDF")) {
						downloadPage.clickDownload();
						logger.info("PDF file downloaded");
					} else if (action.equals("Download Excel")) {
						downloadPage.clickExportIRN();
						logger.info("Excel file downloaded");
					}
					Thread.sleep(5000);

					String downloadPath = System.getProperty("user.dir") + "\\downloadFiles";
					File directoryPath = new File(downloadPath);
					String contents[] = directoryPath.list();
					String oldFileName = contents[0];
					String newFileName = rowData.get("Garden") + "_" + rowData.get("Auc" + aucIndex) + "_"
							+ contents[0];
					File oldFile = new File(System.getProperty("user.dir") + "\\downloadFiles\\" + oldFileName);
					File newFile = null;
					if (action.equals("Download PDF")) {
						newFile = new File(System.getProperty("user.dir") + "\\PDFDownload\\" + newFileName);
					} else if (action.equals("Download Excel")) {
						newFile = new File(System.getProperty("user.dir") + "\\ExcelDownload\\" + newFileName);
					}

					boolean flag = oldFile.renameTo(newFile);

					if (flag == true) {
						logger.info("File successfully renamed for " + rowData.get("Garden") + "_"
								+ rowData.get("Auc" + aucIndex));
					} else {
						logger.info("File rename failed for " + rowData.get("Garden") + "_"
								+ rowData.get("Auc" + aucIndex));
					}

					logger.info("File downloaded for Auction center " + rowData.get("Auc" + aucIndex));
				} else {
					logger.info("No file present for Auction center " + rowData.get("Auc" + aucIndex));
				}
				aucIndex = aucIndex + 1;
			}

			Driver.driverTearDown(driver);
		}

	}

}
