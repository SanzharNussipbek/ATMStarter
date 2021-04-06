<?php
$servername = "127.0.0.1";
$username = "comp4107_grp04";
$password = "993643";
$database = "comp4107_grp04";

// Create connection
$conn = new mysqli($servername, $username, $password, $database);

// Check connection
if (!$conn) {
  die("Connection failed: " . mysqli_connect_error());
}

$req = json_decode($_POST["BAMSReq"], false);

if (strcmp($req->msgType, "LoginReq") === 0) {
  $reply->msgType = "LoginReply";
  $reply->cardNo = $req->cardNo;
  $reply->pin = $req->pin;
  $query = "SELECT PIN FROM CARD WHERE CARD_NUMBER='$req->cardNo'";
  if ($result = mysqli_query($conn, $query)) {
    $db_pin = $result->fetch_row()[0];
    if (strcmp($db_pin, $req->pin) === 0) {
      $reply->cred = "OK";
    } else {
      $reply->cred = "NOK";
    }
    $result->free_result();
  } else {
    $reply->cred = "FAIL";
  }
} else if (strcmp($req->msgType, "GetAccReq") === 0) {
  $reply->msgType = "GetAccReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $query = "SELECT GROUP_CONCAT(ACCOUNT_NO SEPARATOR '/') FROM ACCOUNT WHERE CARD_NUMBER='$req->cardNo'";
  if ($result = mysqli_query($conn, $query)) {
    $reply->accounts = $result->fetch_row()[0];
    $result->free_result();
  } else {
    $reply->accounts = "FAIL";
  }
} else if (strcmp($req->msgType, "WithdrawReq") === 0) {
  $reply->msgType = "WithdrawReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  $query = "UPDATE ACCOUNT SET AMOUNT=AMOUNT-$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
  if (mysqli_query($conn, $query)) {
  } else {
    $reply->cred = "FAIL";
  }
  $reply->outAmount = $req->amount;
} else if (strcmp($req->msgType, "DepositReq") === 0) {
  $reply->msgType = "DepositReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $query = "UPDATE ACCOUNT SET AMOUNT=AMOUNT+$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
  if (mysqli_query($conn, $query)) {
    $reply->amount = $req->amount + $req->depAmount;
    $query = "SELECT AMOUNT FROM ACCOUNT WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
    if ($result = mysqli_query($conn, $query)) {
      $reply->amount = $result->fetch_row()[0];
      $result->free_result();
    } else {
      $reply->amount = "FAIL";
    }
  } else {
    $reply->amount = "FAIL";
  }
  $reply->amount = $req->amount;
  $reply->depAmount = $req->amount;
} else if (strcmp($req->msgType, "EnquiryReq") === 0) {
  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $query = "SELECT AMOUNT FROM ACCOUNT WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
  if ($result = mysqli_query($conn, $query)) {
    $reply->amount = $result->fetch_row()[0];
    $result->free_result();
  } else {
    $reply->amount = "FAIL";
  }
} else if (strcmp($req->msgType, "TransferReq") === 0) {
  $reply->msgType = "TransferReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->fromAcc = $req->fromAcc;
  $reply->toAcc = $req->toAcc;
  $reply->amount = $req->amount;
  $reply->transAmount = $req->amount;
  $query1 = "UPDATE ACCOUNT SET AMOUNT=AMOUNT-$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->fromAcc'";
  $query2 = "UPDATE ACCOUNT SET AMOUNT=AMOUNT+$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->toAcc'";
  if (mysqli_query($conn, $query1) && mysqli_query($conn, $query2)) {
  } else {
    $reply->cred = "FAIL";
  }
}

echo json_encode($reply);

@mysqli_close($conn);
