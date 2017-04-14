library(shiny)
library(datasets)

# Define server logic required to summarize and view the selected
# dataset
function(input, output) {
  
  library(RJDBC)

  drv <- JDBC("com.mysql.jdbc.Driver",
              "/home/conor/Downloads/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar",
              identifier.quote="`")
  
  conn <- dbConnect(drv, "jdbc:mysql://localhost:3310/KingstonGeneralHospital", "root", "")
  
  
  ClickedLinks <-  dbGetQuery(conn, "SELECT * FROM `ClickedLinks` ORDER BY `TimeSpent` DESC")
  SearchQueries <-  dbGetQuery(conn, "SELECT * FROM `SearchQueries` ORDER BY `SearchID` DESC")
  SearchResults <-  dbGetQuery(conn, "SELECT * FROM `SearchResults` ORDER BY `SearchID` DESC")
  
  TopSearched <- dbGetQuery(conn, "SELECT COUNT(*) as Count, Query FROM SearchQueries GROUP BY Query ORDER BY Count DESC")
  TopResults<- dbGetQuery(conn, "SELECT COUNT(*) as Count, SearchResult FROM SearchResults GROUP BY SearchResult ORDER BY Count DESC")
  TopClickedResultsByDomain <- dbGetQuery(conn, "SELECT COUNT(*) as Count,  SUBSTRING_INDEX(Link,'/',3) as Domain FROM ClickedLinks, SearchQueries where SearchQueries.SearchID = ClickedLinks.SearchID GROUP BY SUBSTRING_INDEX(Link,'/',3) ORDER BY COUNT(*)  DESC")
  TopClickedResultsByQuery <- dbGetQuery(conn, "SELECT  Query, COUNT(*) as Count, Link FROM ClickedLinks, SearchQueries where SearchQueries.SearchID = ClickedLinks.SearchID GROUP BY SearchQueries.Query ORDER BY COUNT(*)  DESC")
  
  
  TopSearchedLimited <- dbGetQuery(conn, "SELECT COUNT(*) as Count, Query FROM SearchQueries GROUP BY Query ORDER BY Count DESC LIMIT 10")
  TopResultsLimited<- dbGetQuery(conn, "SELECT COUNT(*) as Count, SearchResult FROM SearchResults GROUP BY SearchResult ORDER BY Count DESC LIMIT 10")
  
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
  


  
  # Show the first "n" observations
  output$view <- renderTable({
    head(datasetInput(), n = input$obs)
  })
  
  output$plot <- renderPlot({
  
    # Render a barplot
    barplot(TopSearchedLimited[,1], names.arg = TopSearchedLimited[,2])
  })
  
  output$plot2 <- renderPlot({
    
    # Render a barplot
    barplot(TopResultsLimited[,1], names.arg = TopResultsLimited[,2], las=1)
  })
}
