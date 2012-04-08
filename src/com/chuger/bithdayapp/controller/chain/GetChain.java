package com.chuger.bithdayapp.controller.chain;

import com.chuger.bithdayapp.controller.chain.chain.Chain;

import java.io.Serializable;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 5:02
 */
public interface GetChain extends Serializable{
    Chain getChain();
}
