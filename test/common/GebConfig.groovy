import org.openqa.selenium.chrome.ChromeDriver

driver = {
    def driverInstance = new ChromeDriver()
    driverInstance.manage().window().maximize()
    driverInstance
}

baseNavigatorWaiting = true
atCheckWaiting = true