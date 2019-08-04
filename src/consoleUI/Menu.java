package consoleUI;

import engine.Commit;
import engine.Manager;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
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
        String option = "";
        while (!quit) {
            System.out.println("Please choose an option:");
            printMenuItems();
            option = scanner.nextLine();

            if (option != "13") {
                try {
                    runCommand(option);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                System.out.println();
            } else {
                quit = true;
            }
        }
    }

    public void buildMenu() {

        addMenuItem("1", "Update user name", new Runnable() {
            @Override
            public void run() {
                //magitManager.UpdateUserName();
            }
        });

        addMenuItem("2", "Load repository", new Runnable() {
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
                    magitManager.switchRepository(repositoryPath+":/"+repositoryName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        addMenuItem("4", "Show all files of current repository", new Runnable() {
            @Override
            public void run() {
                //magitManager.Show all files of current repository();
            }
        });

        addMenuItem("5", "Show status", new Runnable() {
            @Override
            public void run() {
                //magitManager.Show status();
            }
        });

        addMenuItem("6", "Commit", new Runnable() {
            @Override
            public void run() {
                System.out.println("Enter a message for the commit: ");
                String message = scanner.nextLine();
                magitManager.ExcecuteCommit(message);
            }
        });

        addMenuItem("7", "Show all branches", new Runnable() {
            @Override
            public void run() {
                //magitManager.Show all branches();
            }
        });

        addMenuItem("8", "create new branch", new Runnable() {
            @Override
            public void run() {
                //magitManager.Show all files of current repository();
            }
        });

        addMenuItem("9", "Delete branch", new Runnable() {
            @Override
            public void run() {
                //magitManager.Switch repository();
            }
        });

        addMenuItem("10", "Checkout", new Runnable() {
            @Override
            public void run() {
                //magitManager.Show all files of current repository();
            }
        });


        addMenuItem("11", "Show history of active branch", new Runnable() {
            @Override
            public void run() {
                //magitManager.Switch repository();
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
                    magitManager.CreateEmptyRepository(repositoryPath+":/"+ repositoryName);
                } catch (FileAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}