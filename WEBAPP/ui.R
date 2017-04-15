shinyUI(
# Define UI for dataset viewer application
 
fluidPage(
  
  tags$head(
    tags$link(rel="stylesheet", type="text/css",href="styles.css"),
    tags$script(type="text/javascript", src = "md5.js"),
    tags$script(type="text/javascript", src = "passwdInputBinding.js")
    
  ),
  
  # Application title
  headerPanel(img(src='http://www.kgh.on.ca/kgh-journey/images/KGH-Logo.png', align = "left", width = 274, height = 64, display = "block" )
  ),
  
  div(class = "login",
      uiOutput("uiLogin"),
      textOutput("pass"),
      textOutput("Logged")
  ),
  
  
  conditionalPanel(condition ="output.Logged =='Logged in as: Admin'",
  tabsetPanel(
    tabPanel("Tables",
             h3("Tables"),
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
             h3("Charts"),
             
             
             mainPanel(
               tabsetPanel(
                 tabPanel("Top Search Queries", plotOutput("plot", "1600px")),
                 tabPanel("Top Searched (Word Cloud)", plotOutput("plot4")),
                 tabPanel("Average Time Spent By Domain", plotOutput("plot3" ,width = "1600px"))
                 
               )
               
             )
             
    ),
    tabPanel("SQL Query Tool",
             h3("SQL Query Tool"),
             mainPanel(
               
               textInput("text", label = "", width =1000 , value = "Enter SQL Command..."),
               
               tableOutput("queryResult")
               
               
             )
    )
    
  )
  )
    
  
)

)

