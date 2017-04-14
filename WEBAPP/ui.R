library(shiny)
library(wordcloud)
library(tm)
library(RColorBrewer)
library(SnowballC)

# Define UI for dataset viewer application
fluidPage(
  
  tags$head(
    tags$style(HTML("
      @import url('//fonts.googleapis.com/css?family=Lobster|Cabin:400,700');
      
      h1 {
        padding-bottom:20px;
margin-bottom:20px;
      }

    "))
  ),
  
  # Application title
  headerPanel(img(src='http://www.kgh.on.ca/kgh-journey/images/KGH-Logo.png', align = "left", width = 274, height = 64 )
    ### the rest of your code
  ),
  # Sidebar with controls to select a dataset and specify the
  # number of observations to view
  tabsetPanel(
    tabPanel("Tables",
     sidebarLayout(
   
        sidebarPanel(
          selectInput("dataset", "Choose a dataset:", 
                      choices = c("Clicked Links", "Search Queries", "Search Results","Top Searched", "Top Results", "Top Clicked Link by Query", "Top Clicked Link by Domain")),
          
        
          
          numericInput("obs", "Number of observations to view:", 10)
        ),
        mainPanel(
         tableOutput("view")
        )
        )
    ),
    
    tabPanel("Charts",
           
              
            
              mainPanel(
                tabsetPanel(
                  tabPanel("Top Search Queries", plotOutput("plot")),
                  tabPanel("Top Search Results", plotOutput("plot2"))
                )
                  
              )
            
    )
    
  )
)
