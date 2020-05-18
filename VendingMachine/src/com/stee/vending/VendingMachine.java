package com.stee.vending;

import java.util.List;

import com.stee.constants.Coin;
import com.stee.constants.Item;
import com.stee.exception.NotFullPaidException;
import com.stee.exception.NotSufficientChangeException;
import com.stee.exception.SoldOutException;
import com.stee.model.Bucket;

public interface VendingMachine {
	public long selectItemAndGetPrice(Item item) throws SoldOutException;
	public void insertCoin(Coin coin);
	public Bucket<Item, List<Coin>> collectItemAndChange() throws NotSufficientChangeException, NotFullPaidException;
	public List<Coin> refund() throws NotSufficientChangeException;
	public void reset();
	public void printStats();
}
