class Parser {
    private String commandName; //  Command name to be executed
    private String[] args;  //  Args that help the command if needed

    //  A method that parses the input from user and splits it into a command and args
    public boolean parse(String input) {
        String[] parts = input.split("\\s+", 2); // Split on whitespaces, tabs, etc..

        if (parts.length >= 1) {
            commandName = parts[0]; //  Assign first part of input to command name
            if (parts.length > 1) {
                args = parts[1].split("\\s+");  //  Split the arguments into an array of strings
            } else {
                args = new String[0];   //  Empty string array for args
            }
            return true;
        } else {
            return false;
        }
    }

    //  A public method that returns the command name
    public String getCommandName() {
        return commandName;
    }

    //  A public method that returns the args to help execute the command
    public String[] getArgs() {
        return args;
    }
}