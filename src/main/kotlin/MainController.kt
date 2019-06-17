import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.*
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    private lateinit var deviceMenu: Menu
    @FXML
    private lateinit var appManagerMenu: Menu
    @FXML
    private lateinit var secondSpaceButton: CheckMenuItem
    @FXML
    private lateinit var recoveryMenuItem: MenuItem
    @FXML
    private lateinit var reloadMenuItem: MenuItem
    @FXML
    private lateinit var infoTextArea: TextArea
    @FXML
    private lateinit var outputTextArea: TextArea
    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var progressIndicator: ProgressIndicator
    @FXML
    private lateinit var uninstallerTableView: TableView<App>
    @FXML
    private lateinit var reinstallerTableView: TableView<App>
    @FXML
    private lateinit var disablerTableView: TableView<App>
    @FXML
    private lateinit var enablerTableView: TableView<App>
    @FXML
    private lateinit var uncheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var unappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var unpackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var recheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var reappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var repackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var discheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var disappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var dispackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var encheckTableColumn: TableColumn<App, Boolean>
    @FXML
    private lateinit var enappTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var enpackageTableColumn: TableColumn<App, String>
    @FXML
    private lateinit var dpiTextField: TextField
    @FXML
    private lateinit var widthTextField: TextField
    @FXML
    private lateinit var heightTextField: TextField
    @FXML
    private lateinit var partitionComboBox: ComboBox<String>
    @FXML
    private lateinit var scriptComboBox: ComboBox<String>
    @FXML
    private lateinit var autobootCheckBox: CheckBox
    @FXML
    private lateinit var imageLabel: Label
    @FXML
    private lateinit var romLabel: Label
    @FXML
    private lateinit var codenameTextField: TextField
    @FXML
    private lateinit var branchComboBox: ComboBox<String>
    @FXML
    private lateinit var versionLabel: Label
    @FXML
    private lateinit var appManagerPane: TabPane
    @FXML
    private lateinit var reinstallerTab: Tab
    @FXML
    private lateinit var disablerTab: Tab
    @FXML
    private lateinit var enablerTab: Tab
    @FXML
    private lateinit var fileExplorerPane: TitledPane
    @FXML
    private lateinit var camera2Pane: TitledPane
    @FXML
    private lateinit var resolutionPane: TitledPane
    @FXML
    private lateinit var flasherPane: TitledPane
    @FXML
    private lateinit var wiperPane: TitledPane
    @FXML
    private lateinit var oemPane: TitledPane
    @FXML
    private lateinit var dpiPane: TitledPane

    private var image: File? = null
    private var rom: File? = null
    private lateinit var flasher: Flasher

    companion object {
        val version = "6.4"
        lateinit var thread: Thread
    }

    private fun setPanels() {
        when (Device.mode) {
            Mode.ADB -> {
                appManagerPane.isDisable = false
                appManagerMenu.isDisable = false
                reinstallerTab.isDisable = !Device.reinstaller
                disablerTab.isDisable = !Device.disabler
                enablerTab.isDisable = !Device.disabler
                camera2Pane.isDisable = true
                fileExplorerPane.isDisable = false
                resolutionPane.isDisable = false
                dpiPane.isDisable = false
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                deviceMenu.isDisable = false
                recoveryMenuItem.isDisable = false
                reloadMenuItem.isDisable = false
            }
            Mode.RECOVERY -> {
                appManagerPane.isDisable = true
                appManagerMenu.isDisable = true
                reinstallerTab.isDisable = true
                disablerTab.isDisable = true
                enablerTab.isDisable = true
                camera2Pane.isDisable = false
                fileExplorerPane.isDisable = true
                resolutionPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                deviceMenu.isDisable = false
                recoveryMenuItem.isDisable = false
                reloadMenuItem.isDisable = false
            }
            Mode.FASTBOOT -> {
                appManagerPane.isDisable = true
                appManagerMenu.isDisable = true
                camera2Pane.isDisable = true
                fileExplorerPane.isDisable = true
                resolutionPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = false
                wiperPane.isDisable = false
                oemPane.isDisable = false
                deviceMenu.isDisable = false
                recoveryMenuItem.isDisable = true
                reloadMenuItem.isDisable = false
            }
            else -> {
                appManagerPane.isDisable = true
                appManagerMenu.isDisable = true
                camera2Pane.isDisable = true
                fileExplorerPane.isDisable = true
                resolutionPane.isDisable = true
                dpiPane.isDisable = true
                flasherPane.isDisable = true
                wiperPane.isDisable = true
                oemPane.isDisable = true
                deviceMenu.isDisable = true
                recoveryMenuItem.isDisable = true
                reloadMenuItem.isDisable = true
            }
        }
    }

    private fun setUI() {
        setPanels()
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> {
                infoTextArea.text = ""
                infoTextArea.appendText("Serial number:\t\t${Device.serial}\n")
                infoTextArea.appendText("Codename:\t\t${Device.codename}\n")
                infoTextArea.appendText("Bootloader:\t\t")
                if (Device.bootloader)
                    infoTextArea.appendText("unlocked\n")
                else infoTextArea.appendText("locked\n")
                infoTextArea.appendText("Camera2:\t\t\t")
                if (Device.camera2)
                    infoTextArea.appendText("enabled")
                else infoTextArea.appendText("unknown")
            }
            Mode.FASTBOOT -> {
                infoTextArea.text = ""
                infoTextArea.appendText("Serial number:\t\t${Device.serial}\n")
                infoTextArea.appendText("Codename:\t\t${Device.codename}\n")
                infoTextArea.appendText("Bootloader:\t\t")
                if (Device.bootloader)
                    infoTextArea.appendText("unlocked")
                else infoTextArea.appendText("locked")
                if (Device.anti != 0)
                    infoTextArea.appendText("\nAnti version:\t\t${Device.anti}")
            }
            else -> infoTextArea.text = ""
        }
    }

    private fun checkADB(): Boolean {
        val adb = Device.readADB()
        setUI()
        return adb
    }

    private fun checkFastboot(): Boolean {
        val fb = Device.readFastboot()
        setUI()
        return fb
    }

    private fun loadDevice() {
        if ("Looking" !in outputTextArea.text)
            outputTextArea.text = "Looking for devices..."
        reloadMenuItem.isDisable = true
        progressIndicator.isVisible = true
        if (Device.readADB()) {
            progressIndicator.isVisible = false
            val support = Command.exec("adb shell cmd package install-existing xaft")
            Device.reinstaller = !("not found" in support || "Unknown command" in support)
            Device.disabler = "enabled" in Command.exec("adb shell pm enable com.android.settings")
            AppManager.createTables()
            codenameTextField.text = Device.codename
            if (Device.mode == Mode.ADB) {
                dpiTextField.text = if (Device.dpi != -1)
                    Device.dpi.toString()
                else "ERROR"
                widthTextField.text = if (Device.width != -1)
                    Device.width.toString()
                else "ERROR"
                heightTextField.text = if (Device.height != -1)
                    Device.height.toString()
                else "ERROR"
            }
            outputTextArea.text = "Device found in ADB mode!\n\n"
            if (Device.mode == Mode.ADB && (!Device.reinstaller || !Device.disabler))
                outputTextArea.appendText("Note:\nThis device isn't fully supported by the App Manager.\nAs a result, some modules have been disabled.")
            setUI()
            return
        }
        if (Device.readFastboot()) {
            progressIndicator.isVisible = false
            codenameTextField.text = Device.codename
            outputTextArea.text = "Device found in Fastboot mode!"
            setUI()
            return
        }
        if (Device.mode == Mode.AUTH && "Unauthorised" !in outputTextArea.text)
            outputTextArea.text = "Unauthorised device found!\nPlease allow USB debugging!"
        setUI()
    }

    private fun checkDevice() {
        thread = Thread {
            Command.exec("adb start-server")
            while (File(System.getProperty("user.home"), "xaft_tmp").exists()) {
                if (Device.mode != Mode.ADB && Device.mode != Mode.FASTBOOT && Device.mode != Mode.RECOVERY)
                    Platform.runLater { loadDevice() }
                try {
                    Thread.sleep(2000)
                } catch (ie: InterruptedException) {
                    break
                }
            }
        }
        thread.isDaemon = true
        thread.start()
    }

    private fun confirm(msg: String = "Are you sure you want to proceed?", func: () -> Unit) {
        val alert = Alert(AlertType.CONFIRMATION)
        alert.initStyle(StageStyle.UTILITY)
        alert.isResizable = false
        alert.headerText = msg.trim()
        val yes = ButtonType("Yes")
        val no = ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(yes, no)
        val result = alert.showAndWait()
        if (result.get() == yes)
            func()
    }

    override fun initialize(url: URL, rb: ResourceBundle?) {
        outputTextArea.text = "Looking for devices..."
        progressIndicator.isVisible = true
        checkDevice()
        partitionComboBox.items.addAll(
            "boot", "cust", "modem", "persist", "recovery", "system"
        )
        scriptComboBox.items.addAll(
            "Clean install", "Clean install and lock", "Update"
        )
        branchComboBox.items.addAll(
            "Global Stable",
            "Global Developer",
            "China Stable",
            "China Developer",
            "EEA Stable",
            "Russia Stable",
            "India Stable"
        )

        uncheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        uncheckTableColumn.setCellFactory { CheckBoxTableCell() }
        unappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        unpackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        recheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        recheckTableColumn.setCellFactory { CheckBoxTableCell() }
        reappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        repackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        discheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        discheckTableColumn.setCellFactory { CheckBoxTableCell() }
        disappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        dispackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        encheckTableColumn.cellValueFactory = PropertyValueFactory("selected")
        encheckTableColumn.setCellFactory { CheckBoxTableCell() }
        enappTableColumn.cellValueFactory = PropertyValueFactory("appname")
        enpackageTableColumn.cellValueFactory = PropertyValueFactory("packagename")

        uninstallerTableView.columns.setAll(uncheckTableColumn, unappTableColumn, unpackageTableColumn)
        reinstallerTableView.columns.setAll(recheckTableColumn, reappTableColumn, repackageTableColumn)
        disablerTableView.columns.setAll(discheckTableColumn, disappTableColumn, dispackageTableColumn)
        enablerTableView.columns.setAll(encheckTableColumn, enappTableColumn, enpackageTableColumn)

        Command.tic = outputTextArea
        Flasher.tic = outputTextArea
        Flasher.progressInd = progressIndicator
        flasher = Flasher(outputTextArea, progressIndicator)
        AppManager.uninstallerTableView = uninstallerTableView
        AppManager.reinstallerTableView = reinstallerTableView
        AppManager.disablerTableView = disablerTableView
        AppManager.enablerTableView = enablerTableView
        AppManager.progress = progressBar
        AppManager.progressInd = progressIndicator
    }

    private fun checkCamera2(): Boolean = Command.exec("adb shell getprop persist.camera.HAL3.enabled").contains("1")

    private fun checkEIS(): Boolean = Command.exec("adb shell getprop persist.camera.eis.enable").contains("1")

    @FXML
    private fun disableCamera2ButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (Device.mode != Mode.RECOVERY) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            Command.exec("adb shell setprop persist.camera.HAL3.enabled 0")
            outputTextArea.text = if (!checkCamera2())
                "Camera2 disabled!"
            else "ERROR: Couldn't disable Camera2!"
        }
    }

    @FXML
    private fun enableCamera2ButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (Device.mode != Mode.RECOVERY) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            Command.exec("adb shell setprop persist.camera.HAL3.enabled 1")
            outputTextArea.text = if (checkCamera2())
                "Camera2 enabled!"
            else "ERROR: Couldn't enable Camera2!"
        }
    }

    @FXML
    private fun disableEISButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (Device.mode != Mode.RECOVERY) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            Command.exec("adb shell setprop persist.camera.eis.enable 0")
            outputTextArea.text = if (!checkEIS())
                "EIS disabled!"
            else "ERROR: Couldn't disable EIS!"
        }
    }

    @FXML
    private fun enableEISButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            if (Device.mode != Mode.RECOVERY) {
                outputTextArea.text = "ERROR: No device found in recovery mode!"
                return
            }
            Command.exec("adb shell setprop persist.camera.eis.enable 1")
            outputTextArea.text = if (checkEIS())
                "EIS enabled!"
            else "ERROR: Couldn't enable EIS!"
        }
    }

    @FXML
    private fun openButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("FileExplorer.fxml"))
            val parent = fxmlLoader.load<Parent>()
            val scene = Scene(parent)
            val stage = Stage()
            stage.scene = scene
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.scene = scene
            stage.title = "File Explorer"
            stage.isResizable = false
            stage.showAndWait()
        }
    }

    @FXML
    private fun applyDpiButtonPressed(event: ActionEvent) {
        if (dpiTextField.text.trim().isEmpty())
            return
        if (checkADB()) {
            val attempt = Command.exec_displayed("adb shell wm density ${dpiTextField.text.trim()}")
            when {
                "permission" in attempt -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                "bad" in attempt -> {
                    outputTextArea.text = "ERROR: Invalid value!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun resetDpiButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val attempt = Command.exec_displayed("adb shell wm density reset")
            when {
                "permission" in attempt -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun applyResButtonPressed(event: ActionEvent) {
        if (widthTextField.text.trim().isEmpty() || heightTextField.text.trim().isEmpty())
            return
        if (checkADB()) {
            val attempt =
                Command.exec_displayed("adb shell wm size ${widthTextField.text.trim()}x${heightTextField.text.trim()}")
            when {
                "permission" in attempt -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                "bad" in attempt -> {
                    outputTextArea.text = "ERROR: Invalid value!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun resetResButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val attempt = Command.exec_displayed("adb shell wm size reset")
            when {
                "permission" in attempt -> {
                    outputTextArea.text = "ERROR: Please allow USB debugging (Security settings)!"
                }
                attempt.isEmpty() -> {
                    outputTextArea.text = "Done!"
                }
                else -> {
                    outputTextArea.text = "ERROR: $attempt"
                }
            }
        }
    }

    @FXML
    private fun readPropertiesMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                Command.exec_displayed("adb shell getprop")
            }
            Mode.FASTBOOT -> if (checkFastboot()) {
                Command.exec_displayed("fastboot getvar all")
            }
        }
    }

    @FXML
    private fun savePropertiesMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                val props = Command.exec("adb shell getprop")
                val fc = FileChooser()
                fc.extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                fc.title = "Save properties"
                fc.showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)?.let {
                    try {
                        it.writeText(props)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                }
            }
            Mode.FASTBOOT -> if (checkFastboot()) {
                val props = Command.exec("fastboot getvar all")
                val fc = FileChooser()
                fc.extensionFilters.add(FileChooser.ExtensionFilter("Text File", "*"))
                fc.title = "Save properties"
                fc.showSaveDialog((event.target as MenuItem).parentPopup.ownerWindow)?.let {
                    try {
                        it.writeText(props)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        ExceptionAlert(ex)
                    }
                }
            }
        }
    }

    @FXML
    private fun antirbButtonPressed(event: ActionEvent) {
        if (checkFastboot()) {
            val dummy = File("dummy.img")
            dummy.writeBytes(ByteArray(8192))
            val result = Command.exec("fastboot flash antirbpass dummy.img")
            if ("FAILED" in result)
                Command.exec_displayed("fastboot oem ignore_anti")
            else outputTextArea.text = result
            dummy.delete()
        }
    }

    @FXML
    private fun browseimageButtonPressed(event: ActionEvent) {
        val fc = FileChooser()
        fc.extensionFilters.add(FileChooser.ExtensionFilter("Image File", "*.*"))
        fc.title = "Select an image"
        image = fc.showOpenDialog((event.source as Node).scene.window)
        imageLabel.text = image?.name
    }

    @FXML
    private fun flashimageButtonPressed(event: ActionEvent) {
        image?.let {
            partitionComboBox.value?.let { pcb ->
                if (it.absolutePath.isNotEmpty() && pcb.trim().isNotEmpty() && checkFastboot()) {
                    confirm {
                        if (autobootCheckBox.isSelected && pcb.trim() == "recovery")
                            flasher.exec(it, "fastboot flash ${pcb.trim()}", "fastboot boot")
                        else flasher.exec(it, "fastboot flash ${pcb.trim()}")
                    }
                }
            }
        }
    }

    @FXML
    private fun browseromButtonPressed(event: ActionEvent) {
        val dc = DirectoryChooser()
        dc.title = "Select the root directory of a Fastboot ROM"
        outputTextArea.text = ""
        romLabel.text = "-"
        rom = dc.showDialog((event.source as Node).scene.window)
        rom?.let {
            if (File(it, "images").exists()) {
                romLabel.text = it.name
                outputTextArea.text = "Fastboot ROM found!"
                File(it, "flash_all.sh").setExecutable(true, false)
                File(it, "flash_all_lock.sh").setExecutable(true, false)
                File(it, "flash_all_except_storage.sh").setExecutable(true, false)
                File(it, "flash_all_except_data.sh").setExecutable(true, false)
                File(it, "flash_all_except_data_storage.sh").setExecutable(true, false)
            } else {
                outputTextArea.text = "ERROR: Fastboot ROM not found!"
                romLabel.text = "-"
                rom = null
            }
        }
    }

    @FXML
    private fun flashromButtonPressed(event: ActionEvent) {
        rom?.let {
            scriptComboBox.value?.let { scb ->
                if (checkFastboot())
                    confirm {
                        val rf = ROMFlasher(progressBar, progressIndicator, outputTextArea, it)
                        setPanels()
                        when (scb) {
                            "Clean install" -> rf.exec("flash_all")
                            "Clean install and lock" -> rf.exec("flash_all_lock")
                            "Update" -> when {
                                File(
                                    it,
                                    "flash_all_except_data_storage.sh"
                                ).exists() -> rf.exec("flash_all_except_data_storage")
                                File(it, "flash_all_except_data.sh").exists() -> rf.exec("flash_all_except_data")
                                else -> rf.exec("flash_all_except_storage")
                            }
                        }
                    }
            }
        }
    }

    @FXML
    private fun bootButtonPressed(event: ActionEvent) {
        image?.let {
            if (it.absolutePath.isNotEmpty() && checkFastboot())
                flasher.exec(it, "fastboot boot")
        }
    }

    @FXML
    private fun cacheButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            Command.exec_displayed("fastboot erase cache")
    }

    @FXML
    private fun dataButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            confirm("All your data will be gone.\nAre you sure you want to proceed?") { Command.exec_displayed("fastboot erase userdata") }
    }

    @FXML
    private fun cachedataButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            confirm("All your data will be gone.\nAre you sure you want to proceed?") {
                Command.exec_displayed(
                    "fastboot erase cache",
                    "fastboot erase userdata"
                )
            }
    }

    @FXML
    private fun lockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            confirm("Your partitions must be intact in order to successfully lock the bootloader.\nAre you sure you want to proceed?") {
                Command.exec_displayed(
                    "fastboot oem lock"
                )
            }
    }

    @FXML
    private fun unlockButtonPressed(event: ActionEvent) {
        if (checkFastboot())
            Command.exec_displayed("fastboot oem unlock")
    }

    @FXML
    private fun getlinkButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let {
            if (codenameTextField.text.trim().isNotEmpty()) {
                val codename = codenameTextField.text.trim()
                val url = when (it) {
                    "Global Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                    "Global Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                    "China Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                    "China Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
                    "EEA Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_eea_global&b=F&r=eea&n=")
                    "Russia Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_ru_global&b=F&r=global&n=")
                    "India Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_india_global&b=F&r=global&n=")
                    else -> URL("http://google.com")
                }
                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "GET"
                huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
                huc.instanceFollowRedirects = false
                try {
                    huc.connect()
                    huc.disconnect()
                } catch (e: IOException) {
                    return
                }
                huc.getHeaderField("Location")?.let { link ->
                    if ("bigota" in link) {
                        versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                        outputTextArea.text += "\n\n$link\n\nLink copied to clipboard!"
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(link), null)
                    } else {
                        versionLabel.text = "-"
                        outputTextArea.text += "\n\nLink not found!"
                    }
                }
            }
        }
    }

    @FXML
    private fun downloadromButtonPressed(event: ActionEvent) {
        branchComboBox.value?.let {
            if (codenameTextField.text.trim().isNotEmpty()) {
                val codename = codenameTextField.text.trim()
                val url = when (branchComboBox.value) {
                    "Global Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=F&r=global&n=")
                    "Global Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_global&b=X&r=global&n=")
                    "China Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=F&r=cn&n=")
                    "China Developer" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}&b=X&r=cn&n=")
                    "EEA Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_eea_global&b=F&r=eea&n=")
                    "Russia Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_ru_global&b=F&r=global&n=")
                    "India Stable" ->
                        URL("http://update.miui.com/updates/v1/fullromdownload.php?d=${codename}_india_global&b=F&r=global&n=")
                    else -> URL("http://google.com")
                }
                val huc = url.openConnection() as HttpURLConnection
                huc.requestMethod = "GET"
                huc.setRequestProperty("Referer", "http://en.miui.com/a-234.html")
                huc.instanceFollowRedirects = false
                huc.connect()
                huc.disconnect()
                huc.getHeaderField("Location")?.let { link ->
                    if ("bigota" in link) {
                        versionLabel.text = link.substringAfter(".com/").substringBefore('/')
                        outputTextArea.text += "\n\nStarting download in browser..."
                        if ("linux" in System.getProperty("os.name").toLowerCase())
                            Runtime.getRuntime().exec("xdg-open $link")
                        else Desktop.getDesktop().browse(URI(link))
                    } else {
                        versionLabel.text = "-"
                        outputTextArea.text += "\n\nLink not found!"
                    }
                }
            }
        }
    }

    @FXML
    private fun systemMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                Command.exec("adb reboot")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            Mode.FASTBOOT -> if (checkFastboot()) {
                Command.exec("fastboot reboot")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun recoveryMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                Command.exec("adb reboot recovery")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun fastbootMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                Command.exec("adb reboot bootloader")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            Mode.FASTBOOT -> if (checkFastboot()) {
                Command.exec("fastboot reboot bootloader")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun edlMenuItemPressed(event: ActionEvent) {
        when (Device.mode) {
            Mode.ADB, Mode.RECOVERY -> if (checkADB()) {
                Command.exec("adb reboot edl")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
            Mode.FASTBOOT -> if (checkFastboot()) {
                Command.exec("fastboot oem edl")
                outputTextArea.clear()
                infoTextArea.clear()
                loadDevice()
            }
        }
    }

    @FXML
    private fun reloadMenuItemPressed(event: ActionEvent) {
        outputTextArea.clear()
        infoTextArea.clear()
        loadDevice()
    }

    private fun isAppSelected(list: ObservableList<App>): Boolean {
        if (list.isNotEmpty()) {
            for (app in list)
                if (app.selectedProperty().get())
                    return true
            return false
        } else return false
    }

    @FXML
    private fun uninstallButtonPressed(event: ActionEvent) {
        if (isAppSelected(uninstallerTableView.items) && checkADB())
            confirm("Uninstalling apps which aren't listed by default may brick your Device.\nAre you sure you want to proceed?") {
                setPanels()
                val selected = FXCollections.observableArrayList<App>()
                var n = 0
                uninstallerTableView.items.forEach {
                    if (it.selectedProperty().get()) {
                        selected.add(it)
                        n += it.packagenameProperty().get().lines().size
                    }
                }
                AppManager.uninstall(selected, n) {
                    setPanels()
                }
            }
    }

    @FXML
    private fun reinstallButtonPressed(event: ActionEvent) {
        if (isAppSelected(reinstallerTableView.items) && checkADB()) {
            setPanels()
            val selected = FXCollections.observableArrayList<App>()
            var n = 0
            reinstallerTableView.items.forEach {
                if (it.selectedProperty().get()) {
                    selected.add(it)
                    n += it.packagenameProperty().get().lines().size
                }
            }
            AppManager.reinstall(selected, n) {
                setPanels()
            }
        }
    }

    @FXML
    private fun disableButtonPressed(event: ActionEvent) {
        if (isAppSelected(disablerTableView.items) && checkADB())
            confirm("Disabling apps which aren't listed by default may brick your Device.\nAre you sure you want to proceed?") {
                setPanels()
                val selected = FXCollections.observableArrayList<App>()
                var n = 0
                disablerTableView.items.forEach {
                    if (it.selectedProperty().get()) {
                        selected.add(it)
                        n += it.packagenameProperty().get().lines().size
                    }
                }
                AppManager.disable(selected, n) {
                    setPanels()
                }
            }
    }

    @FXML
    private fun enableButtonPressed(event: ActionEvent) {
        if (isAppSelected(enablerTableView.items) && checkADB()) {
            setPanels()
            val selected = FXCollections.observableArrayList<App>()
            var n = 0
            enablerTableView.items.forEach {
                if (it.selectedProperty().get()) {
                    selected.add(it)
                    n += it.packagenameProperty().get().lines().size
                }
            }
            AppManager.enable(selected, n) {
                setPanels()
            }
        }
    }

    @FXML
    private fun secondSpaceButtonPressed(event: ActionEvent) {
        AppManager.user = if (secondSpaceButton.isSelected)
            10
        else 0
        AppManager.createTables()
    }

    @FXML
    private fun addAppsButtonPressed(event: ActionEvent) {
        if (checkADB()) {
            val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("AppAdder.fxml"))
            val parent = fxmlLoader.load<Parent>()
            val scene = Scene(parent)
            val stage = Stage()
            stage.scene = scene
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.scene = scene
            stage.isResizable = false
            stage.showAndWait()
        }
    }

    @FXML
    private fun aboutMenuItemPressed(event: ActionEvent) {
        val linux = "linux" in System.getProperty("os.name").toLowerCase()
        val alert = Alert(AlertType.INFORMATION)
        alert.initStyle(StageStyle.UTILITY)
        alert.title = "About"
        alert.graphic = ImageView("icon.png")
        alert.headerText =
            "Xiaomi ADB/Fastboot Tools\nVersion $version\nCreated by Saki_EU"
        val vb = VBox()
        vb.alignment = Pos.CENTER
        val discord = Hyperlink("Xiaomi Community on Discord")
        discord.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://discord.gg/xiaomi")
            else Desktop.getDesktop().browse(URI("https://discord.gg/xiaomi"))
        }
        discord.font = Font(15.0)
        val twitter = Hyperlink("Saki_EU on Twitter")
        twitter.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://twitter.com/Saki_EU")
            else Desktop.getDesktop().browse(URI("https://twitter.com/Saki_EU"))
        }
        twitter.font = Font(15.0)
        val github = Hyperlink("Repository on GitHub")
        github.onAction = EventHandler {
            if (linux)
                Runtime.getRuntime().exec("xdg-open https://github.com/Saki-EU/XiaomiADBFastbootTools")
            else Desktop.getDesktop().browse(URI("https://github.com/Saki-EU/XiaomiADBFastbootTools"))
        }
        github.font = Font(15.0)
        vb.children.addAll(discord, twitter, github)
        alert.dialogPane.content = vb
        alert.isResizable = false
        alert.showAndWait()
    }
}
