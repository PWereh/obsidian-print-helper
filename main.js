/*
THIS IS THE COMPILED JAVASCRIPT VERSION OF THE PLUGIN
*/
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __export = (target, all) => {
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);

// main.ts
var main_exports = {};
__export(main_exports, {
  default: () => AndroidPrintPlugin
});
module.exports = __toCommonJS(main_exports);
var import_obsidian = require("obsidian");

// Default settings for the plugin
const DEFAULT_SETTINGS = {
  marginTop: "20",
  marginRight: "20",
  marginBottom: "20",
  marginLeft: "20",
  pageSize: "A4",
  showPrintPreview: true
};

// The main plugin class
class AndroidPrintPlugin extends import_obsidian.Plugin {
  async onload() {
    await this.loadSettings();
    this.addRibbonIcon("printer", "Print Active File", (evt) => {
      const activeFile = this.app.workspace.getActiveFile();
      if (activeFile) {
        this.initiatePrint(activeFile);
      } else {
        new import_obsidian.Notice("No active file to print.");
      }
    });
    this.addCommand({
      id: "print-to-android",
      name: "Print current file (Android)",
      callback: () => {
        const activeFile = this.app.workspace.getActiveFile();
        if (activeFile) {
          this.initiatePrint(activeFile);
        } else {
          new import_obsidian.Notice("No active file to print.");
        }
      }
    });
    this.addSettingTab(new AndroidPrintSettingTab(this.app, this));
    console.log("Android Print Plugin loaded.");
  }
  onunload() {
    console.log("Android Print Plugin unloaded.");
  }
  async loadSettings() {
    this.settings = Object.assign({}, DEFAULT_SETTINGS, await this.loadData());
  }
  async saveSettings() {
    await this.saveData(this.settings);
  }
  /**
   * Initiates the print process for a given file.
   * @param file The file to be printed.
   */
  async initiatePrint(file) {
    if (this.settings.showPrintPreview) {
      const fileContent = await this.app.vault.read(file);
      new PrintPreviewModal(this.app, file.basename, fileContent, () => {
        this.executePrint(file);
      }).open();
    } else {
      this.executePrint(file);
    }
  }
  /**
   * This is where the magic happens. This function would need to
   * interface with a native Android module.
   * @param file The file to be printed.
   */
  async executePrint(file) {
    new import_obsidian.Notice(`Printing "${file.basename}"...`);
    this.nativeAndroidPrint(file);
  }
  /**
   * Placeholder for the native Android print call.
   * This function would need to be implemented with a native bridge.
   * @param file The file to be printed.
   */
  nativeAndroidPrint(file) {
    console.log("Attempting to print natively on Android.");
    console.log("File:", file.path);
    console.log("Page Settings:", this.settings);
    const printPayload = {
      filePath: file.path,
      ...this.settings
    };
    new import_obsidian.Notice("Native print function not implemented. See console for details.");
  }
}

// The settings tab for the plugin
class AndroidPrintSettingTab extends import_obsidian.PluginSettingTab {
  constructor(app, plugin) {
    super(app, plugin);
    this.plugin = plugin;
  }
  display() {
    const { containerEl } = this;
    containerEl.empty();
    containerEl.createEl("h2", { text: "Android Print Settings" });
    new import_obsidian.Setting(containerEl).setName("Page Size").setDesc("Select the paper size for printing.").addDropdown((dropdown) => dropdown.addOption("A4", "A4").addOption("Letter", "Letter").addOption("Legal", "Legal").setValue(this.plugin.settings.pageSize).onChange(async (value) => {
      this.plugin.settings.pageSize = value;
      await this.plugin.saveSettings();
    }));
    containerEl.createEl("h3", { text: "Margins (in mm)" });
    new import_obsidian.Setting(containerEl).setName("Top Margin").addText((text) => text.setPlaceholder("e.g., 20").setValue(this.plugin.settings.marginTop).onChange(async (value) => {
      this.plugin.settings.marginTop = value;
      await this.plugin.saveSettings();
    }));
    new import_obsidian.Setting(containerEl).setName("Right Margin").addText((text) => text.setPlaceholder("e.g., 20").setValue(this.plugin.settings.marginRight).onChange(async (value) => {
      this.plugin.settings.marginRight = value;
      await this.plugin.saveSettings();
    }));
    new import_obsidian.Setting(containerEl).setName("Bottom Margin").addText((text) => text.setPlaceholder("e.g., 20").setValue(this.plugin.settings.marginBottom).onChange(async (value) => {
      this.plugin.settings.marginBottom = value;
      await this.plugin.saveSettings();
    }));
    new import_obsidian.Setting(containerEl).setName("Left Margin").addText((text) => text.setPlaceholder("e.g., 20").setValue(this.plugin.settings.marginLeft).onChange(async (value) => {
      this.plugin.settings.marginLeft = value;
      await this.plugin.saveSettings();
    }));
    new import_obsidian.Setting(containerEl).setName("Show Print Preview").setDesc("Show a preview of the content before printing.").addToggle((toggle) => toggle.setValue(this.plugin.settings.showPrintPreview).onChange(async (value) => {
      this.plugin.settings.showPrintPreview = value;
      await this.plugin.saveSettings();
    }));
  }
}

/**
 * A modal to show a preview of the content before printing.
 */
class PrintPreviewModal extends import_obsidian.Modal {
  constructor(app, title, content, onPrint) {
    super(app);
    this.titleText = title;
    this.contentText = content;
    this.onPrint = onPrint;
  }
  onOpen() {
    const { contentEl, titleEl } = this;
    titleEl.setText(`Preview: ${this.titleText}`);
    const previewDiv = contentEl.createDiv({ cls: "print-preview-content" });
    previewDiv.setText(this.contentText);
    new import_obsidian.Setting(contentEl).addButton((btn) => btn.setButtonText("Print").setCta().onClick(() => {
      this.onPrint();
      this.close();
    })).addButton((btn) => btn.setButtonText("Cancel").onClick(() => {
      this.close();
    }));
  }
  onClose() {
    let { contentEl } = this;
    contentEl.empty();
  }
}