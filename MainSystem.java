import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

class User {
    public String name;
    public String email;

    public User(String _name, String _email)
    {
        name = _name;
        email = _email;
    }

    public void print()
    {
        System.out.printf("%s:%s\n", name, email);
    }
}

class meeting {
    public ArrayList<User> users;
    public LocalDateTime startTime;
    DateTimeFormatter dateHour;

    public meeting(ArrayList<User> _users, LocalDateTime _startTime, DateTimeFormatter _dateHour)
    {
        users = _users;
        startTime = _startTime;
        dateHour = _dateHour;
    }

    public void print()
    {
        System.out.printf("Meeting starting at %s participants:\n", startTime.format(dateHour).toString());
        for (User user : users) {
            System.out.print("  ");
            user.print();
        }
    }
}

class MainSystem {
    private HashMap<String, User> users;
    private ArrayList<meeting> meetings;
    DateTimeFormatter dateHour = DateTimeFormatter.ofPattern("dd.MM.yyyy HH");

    public MainSystem()
    {
        users = new HashMap<>();
        meetings = new ArrayList<>();
    }
    public void run()
    {
        Scanner in = new Scanner(System.in);
        boolean end = false;
        while(!end)
        {
            System.out.println("Awaiting new input");
            if(in.hasNextLine())
            {
                String[] input = in.nextLine().trim().split(" ");

                switch(input[0].toLowerCase())
                {
                    case "createuser":
                        String name = "placeholder";
                        String email = "quit";
                        if(input.length == 1)
                        {
                            String emailInput;
                            System.out.println("please input the email of the User");
                            while(in.hasNextLine())
                            {
                                emailInput = in.nextLine().trim().toLowerCase();
                                if(emailInput.equalsIgnoreCase("quit"))
                                {
                                    break;
                                }
                                if(emailInput.split(" ").length == 1 && emailInput.split("@").length == 2 && !users.containsKey(emailInput))
                                {
                                    email = emailInput;
                                    break;
                                } 
                                else
                                {
                                    System.out.printf("The Email: %s  is not a vailed email, Try again or type \"quit\" \n", emailInput );
                                }
                            }
                            if(email.equalsIgnoreCase("quit")) break;
                            System.out.println("please input name of the User");
                            while(in.hasNextLine())
                            {
                                name = in.nextLine().trim();
                                break;
                            }
                        }
                        else if(input.length == 3)
                        {
                            if(input[1].split("@").length != 2)
                            {
                                System.out.printf("The Email: %s  is not a vailed email\n", input[1] );
                            }
                            else if (users.containsKey(input[1].toLowerCase()))
                            {
                                System.out.printf("Email %s already exist in the system\n", input[1] );
                                break;
                            }
                            else
                            {
                                email = input[1].toLowerCase();
                                name = input[2];
                            }
                        }
                        else
                        {
                            System.out.println("Can not create User with the given inputs. Type either \"CreateUser <email> <name>\" or \"CreateUser\" ");
                            break;
                        }
                        users.put(email, new User(name, email));
                        System.out.print("Created new User ");
                        users.get(email).print();
                        break;
                    case "createmeeting":
                        System.out.println("Please input the email of a User to add to the meeting or type \"cancel\" to cancel the meeting creation");
                        String emailInput;
                        ArrayList<User> participants = new ArrayList<User>();
                        boolean cancelMeeting = false;
                        // Loop for adding participants to the meeting
                        while(in.hasNextLine())
                        {
                            {
                                emailInput = in.nextLine().trim().toLowerCase();
                                if(emailInput.equalsIgnoreCase("end") || emailInput.equalsIgnoreCase("cancel"))
                                {
                                    cancelMeeting = emailInput.equalsIgnoreCase("cancel");
                                    break;
                                }
                                if(emailInput.split(" ").length == 1 && emailInput.split("@").length == 2 ) // add single participant
                                {
                                    if(users.containsKey(emailInput) && !participants.contains(users.get(emailInput)))
                                    {
                                        participants.add(users.get(emailInput));
                                        System.out.printf("Added participant %s to the meeting\n", users.get(emailInput).name);
                                    }
                                    else
                                        System.out.printf("The Email %s does not exist in the system\n", emailInput);
                                } 
                                else if (emailInput.split(" ").length > 1) { // add list of participants
                                    for (String _email : emailInput.split(" ")) {
                                        if (_email.split("@").length == 2 && users.containsKey(_email) && !participants.contains(users.get(_email)))
                                        {
                                            participants.add(users.get(_email));
                                            System.out.printf("Added participant %s to the meeting\n", users.get(_email).name);
                                        }
                                        else
                                            System.out.printf("Could not find the email: %s or it is not a vaild email\n", _email);
                                    }
                                } 
                                else
                                {
                                    System.out.printf("The Email: %s  is not a vailed email, Try again or type \"quit\" \n", emailInput );
                                }
                            }
                            System.out.println("Please input the email of a User to add to the meeting or type \"end\" to find a timeslot for the meeting or type \"cancel\" to cancel the meeting creation");
                        }
                        if(cancelMeeting)
                        {
                            System.out.println("Meeting creation has been canceled");
                            break;
                        }

                        //timeslot recommendation
                        ArrayList<LocalDateTime> usedTimesSlots = new ArrayList<>();
                        for (User user : participants) {
                            meetings.forEach(meeting -> {if(meeting.users.contains(user)) usedTimesSlots.add(meeting.startTime);});
                        }

                        
                        LocalDateTime currentTime = LocalDateTime.now().withNano(0).withSecond(0).withMinute(0).plusHours(1);
                        if(currentTime.getHour() < 8)
                            currentTime = currentTime.withHour(8);
                        if(currentTime.getHour() > 16)
                            currentTime = currentTime.plusDays(1).withHour(8);
                        while(true)
                        {
                            if(usedTimesSlots.contains(currentTime))
                                currentTime = currentTime.plusHours(1);
                                if(currentTime.getHour() > 16)
                                {
                                    currentTime = currentTime.plusDays(1).withHour(8);
                                }
                            else
                                break;
                        }
                        System.out.printf("Next possibol meeting slot for all participants at %s\n", currentTime.format(dateHour));
                        
                        currentTime = currentTime.plusDays(1).withHour(8);
                        while(true)
                        {
                            if(usedTimesSlots.contains(currentTime))
                                currentTime = currentTime.plusHours(1);
                                if(currentTime.getHour() > 16)
                                {
                                    currentTime = currentTime.plusDays(1).withHour(8);
                                }
                            else
                                break;
                        }
                        System.out.printf("Next possibol meeting slot at another day for all participants is at %s\n", currentTime.format(dateHour));

                        //add meeting in any timeslot
                        System.out.println("Put timeslot for meeting in the format: \"dd.MM.yyyy HH\"");
                        while(in.hasNextLine())
                        {
                            String timeinput = in.nextLine().trim();
                            if(timeinput.equalsIgnoreCase("cancel"))
                            {
                                System.out.println("Meeting creation has been canceled");
                                break;
                            }
                            try {
                                LocalDateTime dateTimeInput = LocalDateTime.parse(timeinput, dateHour);
                                System.out.printf("Saving meeting at %s\n", dateTimeInput.format(dateHour));
                                meeting meet = new meeting(participants, dateTimeInput, dateHour);
                                meetings.add(meet);
                                meet.print();
                            } catch (Exception e) {
                                System.out.printf("Could not parse: %s , Make sure to input the format: \"dd.MM.yyyy HH\"\n, or cancel", timeinput);
                            }
                            break;
                        }
                        break;
                    case "quit":
                        end = true;
                        break;
                    case "printusers":
                        if(users.size() == 0)
                        {
                            System.out.println("no Users");
                            break;
                        }
                        users.forEach((s, user) -> {user.print();});
                        break;
                    case "printmeetings":
                        if(meetings.size() == 0)
                        {
                            System.out.println("no meetings");
                            break;
                        }
                        if(input.length == 1)
                            meetings.forEach(meeting -> {meeting.print();});
                        else
                        {
                            meetings.forEach(meeting -> {if(meeting.users.contains(users.get(input[1]))) meeting.print();});
                        }
                        break;
                    case "help":
                        System.out.println(
                            "To create a User type either \"CreateUser <email> <name>\" or \"CreateUser\" \n"
                            + "To Create a meeting type \"createMeeting\" \n"
                            + "To Print list of User or meetings type \"printUsers\" or \"printMeetings\" \n"
                            + "To Print list of meetings for a user type \"printMeetings <user email>\" \n"
                            + " To quit type \"quit\" ");
                        break;
                    default:
                        System.out.printf("Could not reconize input: \"%s\" \n try input: \"help\"\n", input[0] );
                        break;
                }

            }
        };
        in.close();
    }

    public static void main(String[] args)
    {
        System.out.println("Program Started");
        MainSystem ms = new MainSystem();
        ms.run();
        System.out.println("Program ended");
    }
}
