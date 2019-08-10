package consoleUI;

import engine.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private ArrayList<MenuItem> menuItems;
    private Scanner scanner;
    private Manager magitManager;

    public Menu() {
        this.menuItems = new ArrayList<MenuItem>();
        this.scanner = new Scanner(System.in);
        magitManager = new Manager();
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

            if (option.equals("13")) {
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
                //magitManager.Load repository();
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
                System.out.println(magitManager.GetRepositoryDetails());
                System.out.println(magitManager.GetStatus());
            }
        });

        addMenuItem("6", "Commit", new Runnable() {
            @Override
            public void run() {
                if (!magitManager.thereAreUncommittedChanges()) {
                    System.out.println("There are no open changes");
                } else {
                    System.out.println("Enter a message for the commit:");
                    String message = scanner.nextLine();
                    magitManager.ExecuteCommit(message);
                }
            }
        });

        addMenuItem("7", "Show all branches", new Runnable() {
            @Override
            public void run() {
                List<String> allBranchesDetails;
                allBranchesDetails = magitManager.GetAllBranchesDetails();
                for (String str : allBranchesDetails) {
                    System.out.println(str);
                }
            }
        });

        addMenuItem("8", "create new branch", new Runnable() {
            @Override
            public void run() {
                String branchName;
                String choice = "";
                System.out.println("Enter the new branch name:");
                branchName = scanner.nextLine();
                System.out.println("Would you like to checkout " + branchName + "?\r\n[1] yes   [2] no");
                choice = scanner.nextLine();
                while (!choice.equals("1") && !choice.equals("2")) {
                    System.out.println("Invalid choice. Please enter '1' or '2'");
                    choice = scanner.nextLine();
                }
                try {
                    magitManager.CreateNewBranch(branchName, choice.equals("1"));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("9", "Delete branch", new Runnable() {
            @Override
            public void run() {
                String branchName;
                System.out.println("Enter the name of the branch you would like to delete:");
                branchName = scanner.nextLine();
                try {
                    magitManager.DeleteBranch(branchName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("10", "Checkout", new Runnable() {
            @Override
            public void run() {
                String branchToCheckout;
                System.out.println("Enter the name of the branch you would like to checkout: ");
                branchToCheckout = scanner.nextLine();
                String choice;
                try {
                    if (magitManager.thereAreUncommittedChanges()) {
                        System.out.println("There are uncommitted changes");
                        System.out.println("Would you like to commit before checkout " + branchToCheckout + "?\r\n[1] yes   [2] no");
                        choice = scanner.nextLine();
                        while (!choice.equals("1") && !choice.equals("2")) {
                            System.out.println("Invalid choice. Please enter '1' or '2'");
                            choice = scanner.nextLine();
                        }
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
                String activeBranchHistory;
                activeBranchHistory = magitManager.GetActiveBranchHistory();
                System.out.println(activeBranchHistory);
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
                    magitManager.CreateEmptyRepository(repositoryPath + ":/" + repositoryName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });


        addMenuItem("13", "Quit", new Runnable() {
            @Override
            public void run() {
                System.out.println("Goodbye!");
            }
        });
    }
}
