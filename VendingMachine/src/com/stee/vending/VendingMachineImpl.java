package com.stee.vending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stee.constants.Coin;
import com.stee.constants.Item;
import com.stee.exception.NotFullPaidException;
import com.stee.exception.NotSufficientChangeException;
import com.stee.exception.SoldOutException;
import com.stee.model.Bucket;
import com.stee.model.Inventory;

public class VendingMachineImpl implements VendingMachine {   
    private Inventory<Coin> cashInventory = new Inventory<Coin>();
    private Inventory<Item> itemInventory = new Inventory<Item>();  
    private long totalSales;
    private Item currentItem;
    private long currentBalance; 
   
    public VendingMachineImpl(){
        initialize();
    }
   
    private void initialize(){       
        //initialize machine with 5 coins of each denomination
        //and 5 cans of each Item 
    	
    	cashInventory.put(Coin.QUARTER, 0);
    	cashInventory.put(Coin.DIME, 0);
    	cashInventory.put(Coin.NICKLE, 0);
    	cashInventory.put(Coin.PENNY, 3);
/*        for(Coin c : Coin.values()){
            cashInventory.put(c, 5);
        }*/
       
    	itemInventory.put(Item.COKE, 5);
    	itemInventory.put(Item.PEPSI, 5);
    	itemInventory.put(Item.SODA, 1);
/*        for(Item i : Item.values()){
            itemInventory.put(i, 5);
        } */     
    }
   
   @Override
    public long selectItemAndGetPrice(Item item) throws SoldOutException{
        if(itemInventory.hasItem(item)){
            currentItem = item;
            return currentItem.getPrice();
        }
        throw new SoldOutException("Sold Out, Please buy another item");
    }

    @Override
    public void insertCoin(Coin coin) {
        currentBalance = currentBalance + coin.getDenomination();
        cashInventory.add(coin);
    }

    @Override
    public Bucket<Item, List<Coin>> collectItemAndChange() throws NotSufficientChangeException, NotFullPaidException {
    	Item item = collectItem();
        totalSales = totalSales + currentItem.getPrice();
        List<Coin> change = collectChange();
        return new Bucket<Item, List<Coin>>(item, change);
    }
       
    private Item collectItem() throws NotSufficientChangeException,
            NotFullPaidException{
        if(isFullPaid()){
            if(hasSufficientChange()){
                itemInventory.deduct(currentItem);
                return currentItem;
            }           
            throw new NotSufficientChangeException("Not Sufficient change in "
            		+ "Inventory");
           
        }
        long remainingBalance = currentItem.getPrice() - currentBalance;
        throw new NotFullPaidException("Price not full paid, remaining : ", 
                                          remainingBalance);
    }
   
    private List<Coin> collectChange() throws NotSufficientChangeException {
        long changeAmount = currentBalance - currentItem.getPrice();
        List<Coin> change = getChange(changeAmount);
        updateCashInventory(change);
        currentBalance = 0;
        currentItem = null;
        return change;
    }
   
    @Override
    public List<Coin> refund() throws NotSufficientChangeException{
    	
    	List<Coin> refund = getChange(currentBalance);
        updateCashInventory(refund);
        currentBalance = 0;
        currentItem = null;
        return refund;
    }
   
   
    private boolean isFullPaid() {
        if(currentBalance >= currentItem.getPrice()){
            return true;
        }
        return false;
    }

      
    private List<Coin> getChange(long amount) throws NotSufficientChangeException{
        List<Coin> changes = Collections.emptyList(); //Using this method provides type safety
       
        if(amount > 0){
            changes = new ArrayList<Coin>();
            long balance = amount;
            while(balance > 0){
                if(balance >= Coin.QUARTER.getDenomination() 
                            && cashInventory.hasItem(Coin.QUARTER)){
                    changes.add(Coin.QUARTER);
                    balance = balance - Coin.QUARTER.getDenomination();
                    continue;
                   
                }else if(balance >= Coin.DIME.getDenomination() 
                                 && cashInventory.hasItem(Coin.DIME)) {
                    changes.add(Coin.DIME);
                    balance = balance - Coin.DIME.getDenomination();
                    continue;
                }else if(balance >= Coin.NICKLE.getDenomination() 
                                 && cashInventory.hasItem(Coin.NICKLE)) {
                    changes.add(Coin.NICKLE);
                    balance = balance - Coin.NICKLE.getDenomination();
                    continue;
                   
                }else if(balance >= Coin.PENNY.getDenomination() 
                                 && cashInventory.hasItem(Coin.PENNY)) {
                    changes.add(Coin.PENNY);
                    balance = balance - Coin.PENNY.getDenomination();
                    continue;
                   
                }else{
                    throw new NotSufficientChangeException("NotSufficientChange, "
                    		+ "Please try another product");
                }
            }
        }
       
        return changes;
    }
   
    @Override
    public void reset(){
        cashInventory.clear();
        itemInventory.clear();
        totalSales = 0;
        currentItem = null;
        currentBalance = 0;
    } 
    
    @Override
    public void printStats(){
        System.out.println("Total Sales : " + totalSales);
        System.out.println("Current Item Inventory : " + itemInventory);
        System.out.println("Current Cash Inventory : " + cashInventory);
    }   
   
    private boolean hasSufficientChange(){
        return hasSufficientChangeForAmount(currentBalance - currentItem.getPrice());
    }
   
    private boolean hasSufficientChangeForAmount(long amount){
        boolean hasChange = true;
        try{
            getChange(amount);
        }catch(NotSufficientChangeException nsce){
            return hasChange = false;
        }
       
        return hasChange;
    }

    private void updateCashInventory(List<Coin> change) {
        for(Coin c : change){
            cashInventory.deduct(c);
        }
    }
   
    public long getTotalSales(){
        return totalSales;
    }
    
    
    public static void main(String[] args) {
    	VendingMachine vm = VendingMachineFactory.createVendingMachine();
    	System.out.println("Printing stats:\n");
    	vm.printStats();
    	try {
    		System.out.println("Selecting Item SODA");
    		vm.selectItemAndGetPrice(Item.SODA);
    		System.out.println("Inserting 2 Quarter Coins");
    		vm.insertCoin(Coin.QUARTER);
    		vm.insertCoin(Coin.QUARTER);
    		
    		Bucket<Item, List<Coin>> bucket=vm.collectItemAndChange();
    		
    		if(bucket!=null) {
        		System.out.println(bucket.getFirst().getName() + " is collected" );
        		if(bucket.getSecond()!=null && bucket.getSecond().size()>0) {
        			System.out.println("Change collected in below denominations");
        			for (Coin c : bucket.getSecond()) {
    					System.out.println(c.getDenomination());
    				}
        		}
        	}
    	
    		System.out.println("Printing stats:\n");
        	vm.printStats();
    		
    		System.out.println("\nSelecting SODA again");
    		vm.selectItemAndGetPrice(Item.SODA);
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	} 
    	
    	System.out.println("Printing stats:\n");
    	vm.printStats();
    	
    	System.out.println("\n\n\nSelecting Pepsi");
    	try {
			vm.selectItemAndGetPrice(Item.PEPSI);
	    	System.out.println("Inserting one Quarter, One Dime and One Nickle");
	    	vm.insertCoin(Coin.QUARTER);
	    	vm.insertCoin(Coin.DIME);
	    	vm.insertCoin(Coin.NICKLE);
	    	System.out.println("Collecting the item");
	    	vm.collectItemAndChange();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
    		try {
				List<Coin> coins = vm.refund();
				System.out.println("Refund collected in below denominations");
    			for (Coin c : coins) {
					System.out.println(c.getDenomination());
				}
			} catch (NotSufficientChangeException e) {
				e.printStackTrace();
			}
    	}
    	
    	try {
    		System.out.println("Printing stats:\n");
        	vm.printStats();
    		System.out.println("\nSelecting Coke");
    		vm.selectItemAndGetPrice(Item.COKE);
    		System.out.println("Inserting 2 Dime");
    		vm.insertCoin(Coin.DIME);
    		vm.insertCoin(Coin.DIME);
    		System.out.println("\nCollecting Item");
    		vm.collectItemAndChange();
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
				List<Coin> coins = vm.refund();
				System.out.println("Refund collected in below denominations");
    			for (Coin c : coins) {
					System.out.println(c.getDenomination());
				}
			} catch (NotSufficientChangeException e) {
				e.printStackTrace();
			}
    	}
    	System.out.println("Printing stats:\n");
    	vm.printStats();
    	
    	
    }
    	
    	
    	/*long itemPrice = 0;
    	try {
    		itemPrice = vm.selectItemAndGetPrice(Item.SODA);
    		System.out.println("Price for " + Item.SODA.getName() + " is " + itemPrice);
    	} catch(SoldOutException e) {
    		e.printStackTrace();
    	}
    	
    	long coinInsertedAmount = 0;
    	
    	while(coinInsertedAmount < itemPrice) {
    		if(itemPrice - coinInsertedAmount >= Coin.QUARTER.getDenomination()) {
    			vm.insertCoin(Coin.QUARTER);
    			coinInsertedAmount+=Coin.QUARTER.getDenomination();
    		} else if(itemPrice - coinInsertedAmount >= Coin.DIME.getDenomination()) {
    			vm.insertCoin(Coin.DIME);
    			coinInsertedAmount+=Coin.DIME.getDenomination();
    		} else if(itemPrice - coinInsertedAmount >= Coin.NICKLE.getDenomination()) {
    			vm.insertCoin(Coin.NICKLE);
    			coinInsertedAmount+=Coin.NICKLE.getDenomination();
    		} else if (itemPrice - coinInsertedAmount < Coin.NICKLE.getDenomination()) {
    			vm.insertCoin(Coin.NICKLE);
    			coinInsertedAmount+=Coin.NICKLE.getDenomination();
    		}
    		else if(itemPrice - coinInsertedAmount >= Coin.PENNY.getDenomination()) {
    			vm.insertCoin(Coin.PENNY);
    			coinInsertedAmount+=Coin.PENNY.getDenomination();
    		}
    	}
    	
    	Bucket<Item, List<Coin>> bucket=null;
    	try {
    		bucket = vm.collectItemAndChange();
		} catch (NotSufficientChangeException e) {
			e.printStackTrace();
		} catch (NotFullPaidException e) {
			e.printStackTrace();
		}
    	
    	if(bucket!=null) {
    		System.out.println(bucket.getFirst().getName() + " is collected" );
    		if(bucket.getSecond()!=null && bucket.getSecond().size()>0) {
    			System.out.println("Change collected in below denominations");
    			for (Coin c : bucket.getSecond()) {
					System.out.println(c.getDenomination());
				}
    		}
    	}*/
    	
   
}
