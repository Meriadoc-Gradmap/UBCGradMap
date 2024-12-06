# Feedback from group *indis-studbud*

We got some suggestions and bugs from group indis-studbud which we will list along with some bugs that we found.

## Bugs before release

We found and patched many bugs before the release on Friday the 29th.

### Prioritized list

1. Database error  
    - Prerequisites weren’t all getting identified correctly. This was our biggest bug since it affected many courses.  
2. Website does not automatically go to https version on mobile  
    - This was a major bug since ubcgradmap.com was unreachable until you typed “https://” on mobile.  
3. Search bar gets super laggy when lots of characters are entered  
    - This bug was easy to find for us, meaning other users will be able to find it too so we prioritized it.  
4. An "Error: Could not load course" message sometimes appears when clicking on a course with no grade information  
    - The error message was ambiguous so we changed it.  
5. Database issue with incorrect prerequisites  
    - This bug only affected a few courses where it would list an incorrect prerequisite which is why it is in the middle of our list.  
6. Revisiting original node resets the zoom level  
    - This wasn’t a major bug so it was not prioritized. It did not affect the usability of our product.  
7. Strange enter key behaviour with search  
    - This wasn’t a major bug so it was not prioritized. The bug did not affect the usability of our product.  
8. Strange behaviour for BMEG 101 and 102  
    - This bug only happened in BMEG 101/102 and there was just an extra arrow connecting them.  
9. HUNU 500  
    - This bug created a link in HUNU 500 that should not be there. It wasn’t prioritized since it did not affect usability.

## Bugs after release

These are the bugs/suggestions that were found after the release. All of the bugs that were found are minor bugs and have not been patched yet. 

### Prioritized list

1. Figure out a plan for recommended prerequisites  
    - There are a few courses with “recommended prerequisites” and we believe that these should be listed in the graph so we need to update our database. This is our top priority because it affects more courses than the other bugs.  
2. APSC 367 \- Double Coreq Link  
    - APSC 367 lists a corequisite twice, so there are 2 corequisite arrows.
3. Sort child nodes
    - Child nodes are currently unsorted and it can be difficult to find a certain one. This is prioritized because it could add some more clairity.
4. No drop shadow  
    - There should be a shadow with the hover names. This bug does not affect usability in any way and it is easy to fix.  
5. Course colour clarity
    - This is not a bug. It is currently not possible to tell if a course isn't offered until clicked on with our current implementation of API calls.
6. Error message precision
    - This is a minor clairity issue which isn't our top priority.
7. Allow opening of a child node without closing parent
    - This is not a bug and is on our list of possible features to implement.
8. All of vs. some of. vs one of
    - We would like to add this, however it would be implemented in a major update and not a patch.
9. Removal of mouse cursor makes hover never go away  
    - This bug is prioritized last because it is very specific and is unlikely to happen to any user.
  
