package ui;

import exceptions.*;
import engine.MagitManager;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private ArrayList<MenuItem> menuItems;
    private Scanner scanner;
    private MagitManager magitManager;

    public Menu() {
        this.menuItems = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        magitManager = new MagitManager();
        buildMenu();
    }

    public void addMenuItem(String key, String name, Runnable runnable) {
        MenuItem menuItem = new MenuItem(key, name, runnable);
        menuItems.add(menuItem);
    }

    private void printMenuItems() {
        for (MenuItem menuItem : menuItems) {
            System.out.println(menuItem.getKey() + "] " + menuItem.getName());
        }
    }

    private void runCommand(String key) throws Exception {
        for (MenuItem i : menuItems) {
            if (i.getKey().equals(key)) {
                i.getRunnable().run();
                return;
            }
        }
        throw new Exception("No valid option for '" + key + "' found, try again.");
    }

    public void run() {
        Boolean quit = false;
        String option;
        while (!quit) {
            System.out.println("Please choose an option:");
            printMenuItems();
            option = scanner.nextLine();

            if (option.equals("14")) {
                quit = true;
            }
            try {
                runCommand(option);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println();
        }
    }

    public void buildMenu() {

        addMenuItem("1", "Update user name", new Runnable() {
            @Override
            public void run() {
                System.out.println("Enter the user name:");
                magitManager.SetUsername(scanner.nextLine());
            }
        });

        addMenuItem("2", "Load repository from XML", new Runnable() {
            @Override
            public void run() {
                String fileFullPath;
                String choice;
                System.out.println("Enter the XML file's full path:");
                fileFullPath = scanner.nextLine();
                try {
                    magitManager.ValidateAndLoadXMLRepository(fileFullPath);
                    System.out.println("The xml file was loaded successfully");
                } catch (XmlRepositoryAlreadyExistsException ex) {
                    System.out.println("The path is already a repository. please choose one of the following options: \r\n" +
                            "[1] replace the existing repository with the one loaded from the xml file\r\n" +
                            "[2] continue working with the existing repository");
                    choice = getUserChoiceBetweenOneAndTwo();
                    if (choice.equals("1")) {
                        try {
                            magitManager.createRepositoryFromMagitRepository();
                            System.out.println("The xml file was loaded successfully");
                        } catch (Exception ex2) {
                            System.out.println("Error occurred while creating the repository:");
                            System.out.println(ex2.getMessage());
                        }
                    } else { // the user chose to stay with the existing repository
                        System.out.println("The xml file was not loaded");
                    }

                } catch (XmlPathContainsNonRepositoryObjectsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error occurred while loading the file:");
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("3", "Switch repository", new Runnable() {
            @Override
            public void run() {
                String repositoryPath;
                String repositoryName;
                System.out.println("Enter the path of the repository you would like to switch to:");
                repositoryPath = scanner.nextLine();
                System.out.println("Enter the name of the repository you would like to switch to:");
                repositoryName = scanner.nextLine();
                try {
                    magitManager.SwitchRepository(repositoryPath + ":/" + repositoryName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("4", "Show all files of current commit", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                List<String> allFilesData = null;
                if (!magitManager.commitsWereExecuted()) {
                    System.out.println("No commits were executed in this repository");
                } else {
                    allFilesData = magitManager.GetDataOfAllFilesOfCurrentCommit();

                    if (allFilesData.isEmpty()) {
                        System.out.println("There are no files in last commit");
                    } else {
                        System.out.println("Current commit's files: ");
                        for (String str : allFilesData) {
                            System.out.println(str);
                        }
                    }
                }
            }
        });

        addMenuItem("5", "Show status", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                System.out.println(magitManager.GetRepositoryDetails());
                try {
                    System.out.println(magitManager.GetWCStatusAsString());
                } catch (IOException e) {
                    System.out.println("Error: runTime failure");
                }
            }
        });

        addMenuItem("6", "Commit", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                try {
                    if (!magitManager.thereAreUncommittedChanges()) {
                        System.out.println("There are no open changes");
                    } else {
                        String theirsLastCommitSha1 = "";
                        System.out.println("Enter a message for the commit:");
                        String message = scanner.nextLine();
                        magitManager.ExecuteCommit(message, theirsLastCommitSha1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addMenuItem("7", "Show all branches", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                List<String> allBranchesDetails;
                try {
                    allBranchesDetails = magitManager.GetAllBranchesDetails();
                    for (String str : allBranchesDetails) {
                        System.out.println(str);
                    }
                } catch (IOException e) {
                    System.out.println("Error: runTime failure");
                }
            }
        });

        addMenuItem("8", "create new branch", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                String branchName;
                String choice = "";
                System.out.println("Enter the new branch name:");
                branchName = scanner.nextLine();
                System.out.println("Would you like to checkout " + branchName + "?\r\n[1] yes   [2] no");
                choice = getUserChoiceBetweenOneAndTwo();

                try {
                    magitManager.CreateNewBranch(branchName, choice.equals("1"), pointToHeadCommit, otherCommitSha1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("9", "Delete branch", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                String branchName;
                System.out.println("Enter the name of the branch you would like to delete:");
                branchName = scanner.nextLine();
                try {
                    magitManager.DeleteBranch(branchName);
                    System.out.println("The branch " + branchName + " was deleted");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("10", "Checkout", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                String branchToCheckout;
                System.out.println("Enter the name of the branch you would like to checkout: ");
                branchToCheckout = scanner.nextLine();
                String choice;
                try {
                    if (magitManager.thereAreUncommittedChanges()) {
                        System.out.println("There are uncommitted changes");
                        System.out.println("Would you like to commit before checkout " + branchToCheckout + "?\r\n[1] yes   [2] no");
                        choice = getUserChoiceBetweenOneAndTwo();

                        if (choice.equals("1")) {
                            runCommand("6");
                        }
                    }
                    magitManager.CheckOut(branchToCheckout);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("11", "Show history of active branch", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                String activeBranchHistory;
                try {
                    activeBranchHistory = magitManager.GetActiveBranchHistory();
                    System.out.println(activeBranchHistory);
                } catch (IOException e) {
                    System.out.println("Error: runTime failure");
                }
            }
        });

        addMenuItem("12", "Create new repository", new Runnable() {
            @Override
            public void run() {
                String repositoryPath;
                String repositoryName;
                try {
                    System.out.println("Enter the path of the new repository:");
                    repositoryPath = scanner.nextLine();
                    System.out.println("Enter the name of the new repository:");
                    repositoryName = scanner.nextLine();
                    magitManager.CreateEmptyRepository(repositoryPath,repositoryName);

                }catch (FileAlreadyExistsException ex){
                    System.out.println(ex.getMessage());
                }catch (Exception e) {
                    System.out.println("you have entered an invalid path");
                }
            }
        });

        addMenuItem("13", "Reset head branch", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.HasActiveRepository()) {
                    System.out.println("Invalid option, the system currently is not working on any repository\r\n" +
                            "Please choose repository to work on, by creating,loading or switching to one");
                    return;
                }
                String sha1;
                String choice;
                try {
                    if (magitManager.thereAreUncommittedChanges()) {
                        System.out.println("There are uncommitted changes");
                        System.out.println("Would you like to commit before reset?\r\n[1] yes   [2] no");
                        choice = getUserChoiceBetweenOneAndTwo();

                        if (choice.equals("1")) {
                            try {
                                runCommand("6");
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error: runTime failure");
                    return;
                }
                System.out.println("Enter the sha1 of the commit you would like to reset the head branch to: ");
                sha1 = scanner.nextLine();
                try {
                    magitManager.ResetHeadBranch(sha1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("14", "Quit", new Runnable() {
            @Override
            public void run() {
                System.out.println("Goodbye!");
            }
        });
    }

    private String getUserChoiceBetweenOneAndTwo() {
        String choice;
        choice = scanner.nextLine();
        while (!choice.equals("1") && !choice.equals("2")) {
            System.out.println("Invalid choice. Please enter '1' or '2'");
            choice = scanner.nextLine();
        }
        return choice;
    }
}
