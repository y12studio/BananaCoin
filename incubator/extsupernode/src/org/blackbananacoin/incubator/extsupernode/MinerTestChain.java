/*
 * Copyright 2013 Y12STUDIO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.blackbananacoin.incubator.extsupernode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

import com.bitsofproof.supernode.common.ByteUtils;
import com.bitsofproof.supernode.common.Hash;
import com.bitsofproof.supernode.common.ValidationException;
import com.bitsofproof.supernode.core.Chain;
import com.bitsofproof.supernode.model.Blk;
import com.bitsofproof.supernode.model.Tx;
import com.bitsofproof.supernode.model.TxIn;
import com.bitsofproof.supernode.model.TxOut;

public class MinerTestChain  implements Chain{
	
	public static final int REWARD_COINS = 70;
	
	static final byte[] SATOSHI_KEY = Hex
			.decode ("04fc9702847840aaf195de8442ebecedf5b095cdbb9bc716bda9110971b28a49e0ead8564ff0db22209e0374782c093bb899692d524e9d6a6956e7c5ecbcd68284");

	static final BigInteger minTarget = BigInteger.valueOf (1).shiftLeft (256).subtract (BigInteger.ONE);

	@Override
	public boolean isSlave ()
	{
		return false;
	}

	@Override
	public boolean checkBeforeCheckpoint ()
	{
		return true;
	}

	@Override
	public boolean allowImmediateCoinbaseSpend ()
	{
		return true;
	}

	@Override
	public BigInteger getMinimumTarget ()
	{
		return minTarget;
	}

	@Override
	public long getMagic ()
	{
		return 0xDAB5BFFAL;
	}

	@Override
	public int getPort ()
	{
		return 8333;
	}

	@Override
	public long getRewardForHeight (int height)
	{
		return (REWARD_COINS * Tx.COIN) >> (height / 150L);
	}

	@Override
	public int getDifficultyReviewBlocks ()
	{
		return 150;
	}

	@Override
	public int getTargetBlockTime ()
	{
		return 1209600;
	}

	@Override
	public byte[] getAlertKey ()
	{
		return SATOSHI_KEY;
	}

	@Override
	public boolean isProduction ()
	{
		return true;
	}

	@Override
	public int getAddressFlag ()
	{
		return 0x00;
	}

	@Override
	public int getP2SHAddressFlag ()
	{
		return 0x05;
	}

	@Override
	public Blk getGenesis ()
	{
		Blk block = new Blk ();

		block.setChainWork (1);
		block.setHeight (0);

		block.setVersion (1);
		block.setCreateTime (1296688602);
		// cpu intel i7 windows 7 
		
		//  [Mine count] mean=161896.8,stddev=119938.72624691881 
		//block.setDifficultyTarget (0x1e7fffff);

		// [Mine count] mean=607.1,stddev=627.4757985594167 
		//block.setDifficultyTarget (0x1fffffff);

		// [Mine count] mean=1364.3,stddev=700.0374672512577 
		block.setDifficultyTarget (0x1f9fffff);
		
		//  [Mine count] mean=382.0,stddev=245.5691801147331 
		// block.setDifficultyTarget (0x1f7fffff);

		//  [Mine count] mean=2.1,stddev=1.3703203194062978 
		//block.setDifficultyTarget (0x207fffff);
		
		block.setNonce (2);
		block.setPreviousHash (Hash.ZERO_HASH_STRING);

		List<Tx> transactions = new ArrayList<Tx> ();
		block.setTransactions (transactions);
		Tx t = new Tx ();
		transactions.add (t);
		t.setBlock (block);
		t.setVersion (1);

		List<TxIn> inputs = new ArrayList<TxIn> ();
		t.setInputs (inputs);
		TxIn input = new TxIn ();
		input.setTransaction (t);
		inputs.add (input);
		input.setSource (null);
		input.setSourceHash (Hash.ZERO_HASH_STRING);
		input.setIx (0L);
		input.setSequence (0xFFFFFFFFL);
		input.setScript (ByteUtils.fromHex ("04" + "ffff001d" + // difficulty target
				"010445" +
				// "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks"
				"5468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73"));

		List<TxOut> outputs = new ArrayList<TxOut> ();
		t.setOutputs (outputs);
		TxOut output = new TxOut ();
		output.setTransaction (t);
		outputs.add (output);
		output.setValue (5000000000L);
		output.setScript (Hex
				.decode ("4104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac"));
		output.setTxHash (t.getHash ());
		output.setHeight (0);
		output.setAvailable (false); // make this explicit
		try
		{
			block.computeHash ();
		}
		catch ( ValidationException e )
		{
		}
		return block;
	}

}
