<?php
$req = json_decode($_POST["BAMSReq"], false);

if (strcmp($req->msgType, "LoginReq") === 0) {
  $reply->msgType = "LoginReply";
  $reply->cardNo = $req->cardNo;
  $reply->pin = $req->pin;
  $reply->cred = "Credible_Credential!!!";
} else if (strcmp($req->msgType, "GetAccReq") === 0) {
  $reply->msgType = "GetAccReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->accounts = "111-222-333/111-222-334/111-222-335/111-222-336";
} else if (strcmp($req->msgType, "WithdrawReq") === 0) {
  $reply->msgType = "WithdrawReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  $reply->outAmount = $req->amount;
} else if (strcmp($req->msgType, "DepositReq") === 0) {
  $reply->msgType = "DepositReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  $reply->depAmount = $req->amount;
} else if (strcmp($req->msgType, "EnquiryReq") === 0) {
  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = "109700";
} else if (strcmp($req->msgType, "TransferReq") === 0) {
  $reply->msgType = "TransferReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->fromAcc = $req->fromAcc;
  $reply->toAcc = $req->toAcc;
  $reply->amount = $req->amount;
  $reply->transAmount = $req->amount;
}

echo json_encode($reply);
?>
