package excbank;
public class InvalidWithdrawException extends Exception
{
   public InvalidWithdrawException()
   {  
   super("low balance");
   } 
  public InvalidWithdrawException(String s)
   {
    super(s);
   }
}
