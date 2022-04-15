1. Maksym Vorobyov; username/password for testing: test@gmail.com/123
2. Files:
	- MiniBank.java - main file that contain all of the code of the project.
	- accountNumbers.txt - a data file which stores encrypted account numbers to exclude a chance of account number coincidence during the registration.
	- email.txt - a data file which stores encrypted emails. It helps to check if an email already used and match account number with the email
	- transactionNumber.txt - stores a number of transaction to make each transaction unique, in addition, it is used for receipts.
	- MTI1NjI=.txt - stores encrypted data about the account
	- MTI1NjI=_receipts.txt - stores encrypted receipts of the account
3. Program imitates working principles of bank. Using it, user can register own account, deposit, withdraw, 
    transfer money, change account details and see the history of transactions (sorted/unsorted) or look for the specific transaction.
4. This program is the second one, the first one was full of  kludges and there was impossible to implement "transfer" method. 
    This version of program and the way it was built seems to me close to good one, 
    however, it could be possible to simplify code greatly if ArrayList and Array classes were allowed.
5. After the running the program user will be asked to create or register an account, and then it will be allowed this person to make transactions.
6. The unique feature of the program can be its encryption that uses Base 64 algorithm . I suppose that it does not meet often in this kind of program.
7. During the coding I was struggling with many issues which includes but not limited to issue with data  file names 
    ( Previously, it used to name files by encrypted email, but when "change email" method was created, it became obvious that it is not the best way. 
    Another problem concerned "encryption" and "decryption" method for some reason the encrypted and decrypted not the array that was passed to the method but the original one. 
    I have asked for the help and understood that the problem was because of passing by reference  - https://stackoverflow.com/questions/11211286/is-it-possible-in-java-to-catch-two-exceptions-in-the-same-catch-block. 
    Also there were a plenty of minor issues but they were fixed easily.
8. After a beta test, another person did not encounter with any issues.
9. After the alpha and beta testing all of the bugs were fixed. Rechecking did not find any problems.
