import excbank.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;
class AtmGui extends Frame implements ActionListener
{ 
  ResultSet rs;
  int pin,acnum;
  Label accntNum,accntPin;
  TextField numInput,pinInput;
  Button loginBtn;
  Connection con;
  Statement st;
  AtmGui()
  {
  accntNum = new Label("Enter Account Number:");
  numInput = new TextField();
  accntPin = new Label("Enter the Pin:");
  pinInput = new TextField();
  loginBtn = new Button("Submit");
  loginBtn.setBackground(Color.green);
  setLayout(null);
  // Bounds for gui elements 
  accntNum.setBounds(40,30,140,30);
  numInput.setBounds(40,70,140,30);
  accntPin.setBounds(40,110,140,30);
  pinInput.setBounds(40,150,140,30); pinInput.setEchoChar('*');
  loginBtn.setBounds(40,200,140,30);
  //adding elements to Frame
  add(accntNum);  add(numInput);
  add(accntPin);  add(pinInput);  add(loginBtn);
  // adding Listener 
  loginBtn.addActionListener(this);
  addWindowListener(new WindowAdapter()
                     {
                     public void windowClosing(WindowEvent w)
                     {
                      System.exit(0);
                     }   
                   });
  //creating connection
  try{
   Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
  con = DriverManager.getConnection("jdbc:odbc:Atmrecords");  
  st= con.createStatement();
  }
  catch(Exception ee){
    System.out.println( ee.getMessage());
  }
  setVisible(true);
  setSize(220,300);
  }
  public void actionPerformed(ActionEvent ae)
  {
    pin = Integer.parseInt(pinInput.getText());
    acnum = Integer.parseInt(numInput.getText());
   try
    {   
      //login verification activity
       String s1= "select * from atmrecord where accountnumber ="+acnum+"and pin="+pin;
       rs= st.executeQuery(s1);
      if(rs.next())
      {
       //creation of object of Frame2 containing Buttons 
        atmButtons abtns = new atmButtons(this);
        abtns.setVisible(true);
        abtns.setSize(220,300);
      }
      else
      {
       JOptionPane.showMessageDialog(this,"No such account exists");
      }
    }
    catch(SQLException se)
    {
      System.err.println("SQL Exception"+ se.getMessage());
    }
    catch(Exception e)
    {
      System.err.println("Exception"+ e.getMessage());
    }
  }
   public static void main(String args[])
  {
   AtmGui a1 = new AtmGui();
  } 
 }
// frame2
class atmButtons extends Frame implements ActionListener
{
  Button balBtn,withdrawBtn,depositBtn,cancelBtn;
  Label head;
  AtmGui ag;
  atmButtons(AtmGui agr)
  { 
    ag=agr;
    withdrawBtn = new Button("Withdraw");
    depositBtn = new Button("Deposit");
    balBtn = new Button("Check Balance");
    cancelBtn = new Button("Cancel");
    head = new Label("what action do you want to perform?");
    setLayout(null);
    // bounds
    head.setBounds(10,40,190,30); 
    balBtn.setBounds(20,80,100,30); balBtn.setBackground(Color.magenta);
    withdrawBtn.setBounds(20,120,100,30);  withdrawBtn.setBackground(Color.cyan);
    depositBtn.setBounds(20,160,100,30);  depositBtn.setBackground(Color.orange);
    cancelBtn.setBounds(20,200,100,30);
    //adding buttons to frame 
    add(balBtn); add(depositBtn); add(withdrawBtn); add(head); add(cancelBtn);
    //adding listeners
    balBtn.addActionListener(this);
    withdrawBtn.addActionListener(this);
    depositBtn.addActionListener(this);  
    cancelBtn.addActionListener(this);
  }
  public void actionPerformed(ActionEvent a2)
  { //to check balance
    if(a2.getSource()==balBtn)
    { 
      String s2= "select Balance from atmrecord where accountnumber ="+ag.acnum+"and pin="+ag.pin;
      try
      {
        ResultSet rs1= ag.st.executeQuery(s2);
        if(rs1.next())
        {
         JOptionPane.showMessageDialog(this,"Current Balance is:"+String.valueOf(rs1.getInt("Balance")));
        }
      }
      catch(SQLException se)
      {
        System.out.println(se.getMessage());
      }
    }
    // to withdraw money
    if(a2.getSource()==withdrawBtn)
    {
      new ActionFrame(this,withdrawBtn);
    } 
    // to deposit amount
    if(a2.getSource()==depositBtn)
    {
      new ActionFrame(this,depositBtn);
    }
    if(a2.getSource()==cancelBtn)
    {
      System.exit(0);
    }
  }
  int withdrawAmount(int amount)
  {
    int balamt= 0;
    try
    {
      if(amount<=0)
         {
          throw new InvalidWithdrawException("Enter a valid amount to withdraw!");
         }
    ResultSet rs2 = ag.st.executeQuery("select Balance from atmrecord where accountnumber ="+ag.acnum+"and pin="+ag.pin);
     if(rs2.next())
        {
         balamt=rs2.getInt("Balance");
         if(amount > balamt)
         {
          balamt=0;
          throw new InvalidWithdrawException("Not enough Balance!");
         }
         else
         {
           balamt= balamt-amount;
           ag.st.executeUpdate("Update atmrecord set Balance ="+balamt+" where accountnumber ="+ag.acnum+" and pin="+ag.pin);
         }
        }
      }
      catch(SQLException s)
     {
       System.out.println(s.getMessage());
     }
     catch(InvalidWithdrawException i)
     {
      System.out.println(i.getMessage());
     }
       return balamt;
  }
   int depositAmount(int amount)
  {
    int balamt=0;
    try
    {
    ResultSet rs2 = ag.st.executeQuery("select Balance from atmrecord where accountnumber ="+ag.acnum+"and pin="+ag.pin);
     if(rs2.next())
        {
         if(amount<=0)
         {
          throw new InvalidWithdrawException("Enter a valid amount to deposit");
         }
         else
         {
           balamt=rs2.getInt("Balance");
           balamt= balamt+amount;
           ag.st.executeUpdate("Update atmrecord set Balance ="+balamt+" where accountnumber ="+ag.acnum+" and pin="+ag.pin);
         }
        }
      }
      catch(SQLException s)
     {
       System.out.println(s.getMessage());
     }
     catch(InvalidWithdrawException i)
     {
       System.out.println(i.getMessage());
     }
       return balamt;
  }
}
class ActionFrame extends Frame implements ActionListener
{
  Button Enterbtn,Exitbtn;
  TextField inputAmt;
  Label l1,l2;
  atmButtons abref;
  Button b3;
  ActionFrame(atmButtons abtns,Button bref)
  { 
    b3=bref;
    abref = abtns;
    Enterbtn = new Button("Enter");
    Exitbtn = new Button("Exit");
    l1= new Label("Enter Amount to Proceed");
    l2= new Label("");
    inputAmt = new TextField();
    setLayout(null);
    // setting position   
    l1.setBounds(30,30,170,30);
    inputAmt.setBounds(30,70,170,30);
    Enterbtn.setBounds(30,110,80,30);
    Exitbtn.setBounds(120,110,80,30);
    l2.setBounds(10,150,200,30);
    Enterbtn.setBackground(Color.green);
    Exitbtn.setBackground(Color.cyan);
    //adding elements to screen
    add(l1); add(inputAmt);
    add(Enterbtn); add(Exitbtn);
    add(l2);
    //adding listeners
    Enterbtn.addActionListener(this);
    Exitbtn.addActionListener(this);
    setSize(220,300);
    setVisible(true);
  }
  public void actionPerformed(ActionEvent av)
  {
    if(av.getSource()==Enterbtn && b3== abref.withdrawBtn)
    {
      int amount= Integer.parseInt(inputAmt.getText());
      // call withdrawAmount method
      int rembal=abref.withdrawAmount(amount);
      if(rembal==0)
      {
        l2.setText("Enter a valid amount to withdraw");
      }
      else
      {
        l2.setText("Remaining Balance:"+String.valueOf(rembal));
      }
    }
    if(av.getSource()==Enterbtn && b3== abref.depositBtn)
    {
      int amount= Integer.parseInt(inputAmt.getText());
      // call depositAmount method
      int rembal=abref.depositAmount(amount);
       if(rembal==0)
      {
        l2.setText("Enter a valid amount to deposit");
      }
      else
      {    
          l2.setText("Balance :"+String.valueOf(rembal));
      }
    }
    if(av.getSource()==Exitbtn)
    {
      dispose();
    }
  }
}
