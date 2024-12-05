# Evaluation of PROcrastination

*In this writeup, Team A should describe how they went about identifying issues (for example, starting with Team B's documentation and testing instructions). If something was hard to test when it should not have been hard, indicate so.*

## How we went about finding issue

We tested edge cases by trying inputs of different shapes and sizes, such as really long inputs and empty inputs. We tested for unicode support, and we tried to push all features to their extremes. We tried doing actions many many times to make sure it would still function. We tried both sequences of events that a normal user would do, and sequences of events that may not be immediately obvious. We tested the networking by joining groups and updating our scores concurrently, as well as signing in to the same account from multiple computers to make sure it updated properly.

Furthermore we tested developer friendly inputs to check to see if they had files on their database that should not be there. 

## Bugs that we found

1. **Emoji usernames give null as “welcome name”**  
- We found that emojis could be inputted as a username, making the displayname “null”  
2. **“Secret” database values still in system – should not be there**  
- Testing Groups were found while inputting invalid group ids  
3. **Group Name lengths have no limits**  
- Making a 1000 word group name causes the text to display incorrectly in certain places on the app  
4. **Tells malicious users all other emails**  
- If you input an email that already exists, there is a message that says that the email exists. This could be used maliciously  
5. **Can use the app without an account**  
- Not 100% sure how this one worked but after signing out I was able to reenter the application   
6. **Random loading icon on pages**  
- In non-loading screens there is a loading icon – this might be a UI element but it makes no sense  
7. **Username lengths have no limits**   
- This causes other screens to be filled with text and is a massive amount of spam   
8. **Multiple groups can be spammed creating as many groups as possible – DDoS attack**  
- Pressing *create group* does not give a timeout as such you can add as many groups as possible, using an autoclicker you can add as many as possible, as there is no size limit on these   
- Since there a possibility of 100,000,000 groups, if there are multiple simultaneous attacks, the app could run out of group ids in around 2 days  
9. **API key is exposed**  
- this might just be a problem with their github implementation   
10. **APP crashes on leaderboard load and creates Session.**  
- User scores larger than the unsigned 32-bit integer limit cannot be parsed from strings to integers. This throws a NumberFormatException and the program crashes immediately. This problem also occurs when inputting a time for creating a session.  
11. **After making the group code you cannot see it ever again**  
- This could should display in a group menu for easier access  
12. **Time unit is unclear \-\> this is a problem as the app requires time**   
- As a user, it is confusing which time unit is used for an input  
13. **Integer overflow on study time causes the app to crash when clicking on set study time**  
- Data is stored as a string in firebase as such there is no limit, however when we go back into java code on a firebase pull it results in an integer overflow crashing the entire app  
14. **Screen rotation messes up the ui elements**  
- After rotating the phone screen to landscape, the ui becomes unusable