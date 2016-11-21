var EC = protractor.ExpectedConditions;
var Objects = require('../json/Objects.json');
var util = require('../util/utils.js');
var wait = require('../util/waitHelper');
var newBtn = element(by.xpath(Objects.basepage.locators.newButton));
var root = element(by.xpath(Objects.basepage.locators.root));
var newCorrespondence = element(by.xpath(Objects.basepage.locators.newCorrespondence));
var docTitle = element(by.xpath(Objects.basepage.locators.docTitle));
var docExtension = element(by.xpath(Objects.basepage.locators.docExtension));
var docType = element(by.xpath(Objects.basepage.locators.docType));
var docCreated = element(by.xpath(Objects.basepage.locators.docCreated));
var docModified = element(by.xpath(Objects.basepage.locators.docModified));
var docAuthor = element(by.xpath(Objects.basepage.locators.docAuthor));
var docVersion = element(by.xpath(Objects.basepage.locators.docVersion));
var docStatus = element(by.xpath(Objects.basepage.locators.docStatus));
var deleteDoc = element(by.xpath(Objects.basepage.locators.deleteDoc));
var downloadDoc = element(by.xpath(Objects.basepage.locators.downloadDoc));
var docRow = element(by.xpath(Objects.basepage.locators.docRow));
var fancyTreeExpandTop = element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop));
var root = element(by.xpath(Objects.taskspage.locators.root));
var notesLink = element(by.xpath(Objects.casepage.locators.notesLink));
var addNoteBtn = element(by.xpath(Objects.casepage.locators.addNoteBtn));
var noteTextArea = element(by.model(Objects.casepage.locators.noteTextArea));
var saveNoteBtn = element(by.xpath(Objects.casepage.locators.saveNoteBtn));
var addedNoteName = element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0);
var deleteNoteBtn = element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editNoteBtn = element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var addNewTaskBtn = element(by.xpath(Objects.casepage.locators.addNewTaskBtn));
var tasksLinkBtn = element(by.xpath(Objects.casepage.locators.tasksLink));
var refreshBtn = element(by.xpath(Objects.casepage.locators.refreshBtn));
var taskTitle = element(by.xpath(Objects.casepage.locators.taskTitle));
var taskAssighnee = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(1);
var taskCreated = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(2);
var taskPriority = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(3);
var taskDueDate = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(4);
var taskStatus = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(5);


var BasePage = function() {

    this.navigateToURL = function(url) {

        browser.get(url);

    };

    this.getPageTitle = function() {

        return browser.getTitle();

    };
    this.clickNewButton = function() {
        browser.waitForAngular().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.newButton))), 30000).then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newButton))), 30000).then(function() {
                    browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.basepage.locators.newButton))), 30000).then(function() {
                        newBtn.click();
                    });
                });
            });
        });
        return this;
    };

    this.clickNewCorrespondence = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newCorrespondence))), 30000).then(function() {
            newCorrespondence.click();
            return this;
        });
    };
    this.selectCorrespondence = function(type, correspondence) {
        var xPathStr;
        if (type == "case") {
            xPathStr = ".//li[@data-command='template/";
        } else {
            xPathStr = ".//li[@data-command='template/Complaint";
        }
        var completexPath;
        switch (correspondence) {
            case "General Release":
                completexPath = xPathStr + "GeneralRelease.docx']";
                break;
            case "Medical Release":
                completexPath = xPathStr + "MedicalRelease.docx']";
                break;
            case "Clearance Granted":
                completexPath = xPathStr + "ClearanceGranted.docx']";
                break;
            case "Clearance Denied":
                completexPath = xPathStr + "ClearanceDenied.docx']";
                break;
            case "Notice of Investigation":
                completexPath = xPathStr + "NoticeofInvestigation.docx']";
                break;
            case "Witness Interview Request":
                completexPath = xPathStr + "InterviewRequest.docx']";
                break;
            default:
                completexPath = xPathStr + "GeneralRelease.docx']";
                break;
        }

        var el = element(by.xpath(completexPath));
        el.click();
        return this;
    };
    this.addCorrespondence = function(correspondence) {
        this.clickNewCorrespondence();
        this.selectCorrespondence(correspondence);
    };
    this.returnDocTitleGrid = function() {

        return docTitle.getText();
    };
    this.returnDocExtensionGrid = function() {
        return docExtension.getText();
    };
    this.returnDocTypeGrid = function() {
        return docType.getText();
    };
    this.returnDocCreatedGrid = function() {
        return docCreated.getText();
    };
    this.returnDocModifiedGrid = function() {
        return docModified.getText();
    };
    this.returnDocAuthorGrid = function() {
        return docAuthor.getText();
    };
    this.returnDocVersionGrid = function() {
        return docVersion.getText();
    };
    this.returnDocStatusGrid = function() {
        return docStatus.getText();
    };
    this.clickDeleteDoc = function() {
        browser.waitForAngular().then(function() {
            docTitle.click().then(function() {
                browser.actions().click(protractor.Button.RIGHT).perform().then(function() {
                    deleteDoc.click();
                    return this;
                });
            });
        });

    };
    this.clickDownloadDoc = function() {
        downloadDoc.click();
        return this;
    };
    this.returnDocRowAdded = function() {
        if (docRow.isPresent()) {
            return true;
        } else {
            return false;
        }
    };
    this.validateDocGridData = function(added, doctitle, docextension, doctype, createddate, modifieddate, author, version, status) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.docAuthor))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docAuthor))), 30000).then(function() {
                expect(this.returnDocRowAdded()).toBe(added);
                expect(this.returnDocTitleGrid()).toEqual(doctitle);
                expect(this.returnDocExtensionGrid()).toEqual(docextension);
                expect(this.returnDocTypeGrid()).toEqual(doctype);
                expect(this.returnDocCreatedGrid()).toEqual(createddate);
                expect(this.returnDocModifiedGrid()).toEqual(modifieddate);
                expect(this.returnDocAuthorGrid()).toEqual(author);
                expect(this.returnDocVersionGrid()).toEqual(version);
                expect(this.returnDocStatusGrid()).toEqual(status);
            });
        })

    }
    this.switchToIframes = function() {
        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 30000);
        browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
            browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
        });
        return this;
    };
    this.switchToDefaultContent = function() {

        browser.driver.switchTo().defaultContent();
        return this;

    };
    this.rightClickRootFolder = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.root))), 30000).then(function() {
            root.click();
            browser.actions().click(protractor.Button.RIGHT).perform();
        });
        return this;
    }
    this.clickExpandFancyTreeTopElementAndSubLink = function(link) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop))), 30000).then(function() {
            fancyTreeExpandTop.click().then(function() {
                var xPathStr = "//span[contains(text(),'";
                var completexPath;
                completexPath = xPathStr + link + "')]";
                browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000).then(function() {
                    var el = element(by.xpath(completexPath));
                    el.click();
                });
                return this;
            });
        });

    };
    this.clickNotesLink = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.notesLink))), 30000);
        notesLink.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNoteBtn))), 30000);
        });
        return this;
    };
    this.addNote = function(note) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNoteBtn))), 30000).then(function() {
            addNoteBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.noteTextArea))), 30000).then(function() {
                    noteTextArea.click().then(function() {
                        noteTextArea.sendKeys(note).then(function() {
                            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000).then(function() {
                                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000).then(function() {
                                    saveNoteBtn.click().then(function() {
                                        browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0)), 30000);
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };
    this.editNote = function(note) {
        browser.wait(EC.invisibilityOf(element(by.xpath(Objects.basepage.locators.fadeElementEditNote))), 30000).then(function() {
            browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000).then(function() {
                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000).then(function() {
                    browser.wait(EC.elementToBeClickable(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000).then(function() {
                        editNoteBtn.click().then(function() {
                            noteTextArea.clear().then(function() {
                                noteTextArea.sendKeys(note).then(function() {
                                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000).then(function() {
                                        saveNoteBtn.click().then(function() {
                                            browser.wait(EC.textToBePresentInElement((addedNoteName), Objects.casepage.data.editnote), 30000);
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };
    this.returnNoteName = function() {
        return addedNoteName.getText();

    };
    this.deleteNote = function() {
        browser.wait(EC.invisibilityOf(element(by.xpath(Objects.basepage.locators.fadeElementDeleteNote))), 30000).then(function() {
            browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000).then(function() {
                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000).then(function() {
                    browser.wait(EC.elementToBeClickable(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000).then(function() {
                        deleteNoteBtn.click();
                    });
                });

            });
        });
        return this;
    };
    this.editNote = function(note) {
        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 10000).then(function() {
            browser.wait(EC.elementToBeClickable(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 10000).then(function() {
                editNoteBtn.click().then(function() {
                    noteTextArea.clear().then(function() {
                        noteTextArea.sendKeys(note).then(function() {
                            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 10000).then(function() {
                                saveNoteBtn.click().then(function() {
                                    browser.wait(EC.textToBePresentInElement((addedNoteName), Objects.casepage.data.editnote), 10000);
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };

    this.clickAddTaskButton = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNewTaskBtn))), 30000).then(function() {
            addNewTaskBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.id(Objects.taskpage.locators.subject))), 30000);
            });
        });

        return this;
    };
    this.clickTasksLinkBtn = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksLink))), 20000).then(function() {

            tasksLinkBtn.click();
        });

        return this;

    };
    this.waitForTasksTable = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksTable))), 30000).then(function() {
            refreshBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.taskTitle))), 30000, "After 30 second task is not shown in the task table");
            });
        });
        return this;

    };
    this.returnTaskTitle = function() {
        return taskTitle.getText();
    };
    this.returnTaskTableAssignee = function() {
        return taskAssighnee.getText();
    };
    this.returnTaskTableCreatedDate = function() {
        return taskCreated.getText();
    };
    this.returnTaskTablePriority = function() {
        return taskPriority.getText();
    };

    this.returnTaskTableDueDate = function() {
        return taskDueDate.getText();
    };

    this.returnTaskTableStatus = function() {
        return taskStatus.getText();
    };


};

module.exports = new BasePage();
