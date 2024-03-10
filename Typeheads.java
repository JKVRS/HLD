Typeaheads refers to the suggestions that come up automatically as we search for something.
You may have observed this while searching on Google, Bing, Amazon Shopping App, etc.

1. Minimum Viable Product:(Similer to gather Requirments)
Questions from the Engineering Architect:
- Popularity of a search phrase is essentially how frequently do people search for
that search phrase. It’s combination of frequency of search, and recency. For
now, assume popularity of a search term is decided by the number of times the
search phrase was searched.
- Strict prefix
-Keep some minimum number of characters post which suggestions will be shown.
-Support for special characters not required at this stage.
- For example, Roger Binny has the highest search frequency: 1 million searches
over the last 5 years. On a daily basis, it receives 1000 searches.
- But, yesterday Roger Federer won Wimbledon and he has received 10000
queries since then. So, the algorithm should ideally rank Roger Federer higher.

Estimate Scale:
Assumptions:
● Search terms or Search queries refers to the final query generated after pressing Enter
or the search button.
● Google receives 10 billion search queries in a day.
● The above figure translates to 60 billion typeahead queries in a day if we assume each
search query triggers six typeahead queries on average.

Need of Sharding?
Next task is to decide whether sharding is needed or not. For this we have to get an estimate of
how much data we need to store to make the system work.
First, let’s decide what we need to store.
● We can store the search terms and the frequency of these search terms.
Assumptions:
● 10% of the queries received by Google every day contain new search terms.
○ This translates to 1 billion new search terms every day.
○ Means 365 billion new search terms every year.
● Next, assuming the system is working past 10 years:
○ Total search terms collected so far: 10 * 365 Billion
● Assuming one search term to be of 32 characters (on average), it will be of 32 bytes.
● Let’s say the frequency is stored in 8 bytes. Hence, total size of a row = 40 bytes.
Total data storage size (in 10 years): 365 * 10 * 40 billion bytes = 146 TB (Sharding is needed).
Read or Write heavy system
● 1 write per 6 reads.
○ This is because we have assumed 10 billion search queries every day which
means there will be 10 billion writes per day.
○ Again each search query triggers 6 typeahead queries => 6 read requests.
● Both a read and write-heavy system.
Note: Read-heavy systems have an order of magnitude higher than writes, so that the writes
don’t matter at all.

Design Goals:
-Availability is more important than consistency.
● Latency of getting suggestions should be super low - you are competing with typing
speed.

APIs
● getSuggestion(prefix_term, limit = 5)
● updateFrequency(search_term)
○ Asynchronous Job performed via an internal call
○ The Google service which provides search results to a user’s query makes an
internal call to Google’s Typeahead service to update the frequency of the search
term.



