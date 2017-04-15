library(shiny)
library(datasets)
library(tm)
library(SnowballC)
library(wordcloud)
library(RColorBrewer)

Logged = FALSE;
PASSWORD <- data.frame(Brukernavn = "admin", Passord = "password")


# Define server logic required to summarize and view the selected
# dataset
shinyServer(function(input, output) {
  source("Login.R", local = TRUE)
  
  output$Logged <- renderText("")
  library(RJDBC)
  
  

  drv <- JDBC("com.mysql.jdbc.Driver",
              "/home/conor/Downloads/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar",
              identifier.quote="`")
  
  conn <- dbConnect(drv, "jdbc:mysql://localhost:3310/KingstonGeneralHospital", "root", "root")
  
  
  autoInvalidate <- reactiveTimer(intervalMs = 3000, session = getDefaultReactiveDomain())
  
  observe({
    
    
  if (USER$Logged == TRUE) {
  
    output$Logged <- renderText("Logged in as: Admin")
    
    
  ClickedLinks <-  dbGetQuery(conn, "SELECT * FROM `ClickedLinks` ORDER BY `TimeSpent` DESC")
  SearchQueries <-  dbGetQuery(conn, "SELECT * FROM `SearchQueries` ORDER BY `SearchID` DESC")
  SearchResults <-  dbGetQuery(conn, "SELECT * FROM `SearchResults` ORDER BY `SearchID` DESC")
  
  TopSearched <- dbGetQuery(conn, "SELECT COUNT(*) as Count, Query FROM SearchQueries GROUP BY Query ORDER BY Count DESC")
  TopResults<- dbGetQuery(conn, "SELECT COUNT(*) as Count, SearchResult FROM SearchResults GROUP BY SearchResult ORDER BY Count DESC")
  TopClickedResultsByDomain <- dbGetQuery(conn, "SELECT COUNT(*) as Count,  SUBSTRING_INDEX(Link,'/',3) as Domain FROM ClickedLinks, SearchQueries where SearchQueries.SearchID = ClickedLinks.SearchID GROUP BY SUBSTRING_INDEX(Link,'/',3) ORDER BY COUNT(*)  DESC")
  TopClickedResultsByQuery <- dbGetQuery(conn, "SELECT  Query, COUNT(*) as Count, Link FROM ClickedLinks, SearchQueries where SearchQueries.SearchID = ClickedLinks.SearchID GROUP BY SearchQueries.Query ORDER BY COUNT(*)  DESC")
  
  
  TopSearchedLimited <- dbGetQuery(conn, "SELECT COUNT(*) as Count, Query FROM SearchQueries GROUP BY Query ORDER BY Count DESC LIMIT 10")
  TopResultsLimited<- dbGetQuery(conn, "SELECT COUNT(*) as Count, SUBSTRING_INDEX(SearchResult,':',1)FROM SearchResults GROUP BY SearchResult ORDER BY Count DESC LIMIT 10")
  
  AverageTimeByDomain <- TopClickedResultsByDomain <- dbGetQuery(conn, "SELECT AVG(TimeSpent) as AverageTime,  SUBSTRING_INDEX(SUBSTRING_INDEX(Link,'/',3), '/',-1) as Domain FROM ClickedLinks, SearchQueries where SearchQueries.SearchID = ClickedLinks.SearchID GROUP BY SUBSTRING_INDEX(Link,'/',3) ORDER BY AverageTime DESC LIMIT 10")
  

  # Return the requested dataset
  datasetInput <- reactive({
    switch(input$dataset,
           "Clicked Links" = ClickedLinks,
           "Search Queries" = SearchQueries,
           "Search Results" = SearchResults,
           "Top Searched" = TopSearched,
           "Top Results" = TopResults,
           "Top Clicked Link by Query" = TopClickedResultsByQuery,
           "Top Clicked Link by Domain" = TopClickedResultsByDomain)
    
  })
  


  
    autoInvalidate()
  # Show the first "n" observations
  output$view <- renderTable({
    head(datasetInput(), n = input$obs)
  })
  
  
  output$plot <- renderPlot({
  
    # Render a barplot
    autoInvalidate()
    barplot(TopSearchedLimited[,1], names.arg = TopSearchedLimited[,2],col=c("#0069aa"))
  })
  
  output$plot2 <- renderPlot({
    autoInvalidate()
    # Render a barplot
    barplot(TopResultsLimited[,1], names.arg = TopResultsLimited[,2], las=1)
  })
  
  output$plot3 <- renderPlot({
    autoInvalidate()
    # Render a barplot
    barplot(AverageTimeByDomain[,1], names.arg = AverageTimeByDomain[,2], width =100 , las=1,type="o", ylab="AverageTime", col=c("#d31245"))
    
  })
  
  wordcloud_rep <- repeatable(wordcloud)
  output$plot4<- renderPlot({
    autoInvalidate()
    #For the word cloud
    Queries <- dbGetQuery(conn, "SELECT Query FROM SearchQueries")
    docs <- Corpus(VectorSource(Queries))
    
    dtm <- TermDocumentMatrix(docs)
    m <- as.matrix(dtm)
    v <- sort(rowSums(m),decreasing=TRUE)
    d <- data.frame(word = names(v),freq=v)
    
    set.seed(1234)
    wordcloud_rep(words = d$word, freq = d$freq, min.freq = 1,
              min.words=20, random.order=FALSE, use.r.layout=FALSE,
              colors=brewer.pal(200, "Dark2"), scale=c(20,0.4))
    
    
   
    
  })

  
    output$queryResult <- renderTable({
      query <- dbGetQuery(conn, input$text)
      head(query, n=10)
    })

  
 
  }
  
  })
  
  
}
)
