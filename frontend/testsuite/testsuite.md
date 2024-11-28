### Current Status
All tests functional. 11/24/2024


### Manual Test Suite for the Front End

1. <b>On load: </b>
![onload.png](imgs%2Fonload.png)
On load the page should contain firstly an opening to CPEN 211, alongside all the loaded post and prerequisites. On the side it should have the Credits, Hours, Average and Description.
2. <b> On Click of background </b>
![backgroundclick.png](imgs%2Fbackgroundclick.png)
On the background click it should show a grey circle.
3. <b> On Click of CPEN 331 </b>
 ![cpen331click.png](imgs%2Fcpen331click.png)
 On the click of CPEN 311 it should show the prerequisites and post requisites 

CPEN 212 should be a redder color as it has a lower average. CPEN 491 should have a greener color as it has a higher average.

4. <b> Clicking back on CPEN 221</b>
![backclick221.png](imgs%2Fbackclick221.png)

It should look just like the original screen when you click back. We note it makes the 221 Selected and 331 Not-Selected

5. <b> Searching Autofill</b>
![autofill.png](imgs%2Fautofill.png)
When searching there should be an autofill. For example searching "APSC 1" should provide APSC 100, APSC 101, APSC 107, APSC 110, APSC 122

6. <b> Pressing Enter on an Invalid Course </b>
![Invalid Course Enter.png](imgs%2FInvalid%20Course%20Enter.png)

![invalid course post enter.png](imgs%2Finvalid%20course%20post%20enter.png)

On enter of APSC 199 it should automatically swap and load APSC 179.

APSC 179 has not pre and post requisites so it should look like a black dot.

7. <b> Burpsuite View of Get Requests </b>

- On load of the website it should get all course names and get the course titles and load CPEN 211 by default
![burpsuiteonload.png](imgs%2Fburpsuiteonload.png)

- On view of get requests it should show the courses being loaded. 
![burpsuitegetrequestss.png](imgs%2Fburpsuitegetrequestss.png)