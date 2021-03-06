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

function validateCred($conn, $userCred, $cardNo)
{
  $query = "SELECT CREDENTIAL FROM CARD WHERE CARD_NUMBER='$cardNo'";
  if ($result = mysqli_query($conn, $query)) {
    $cred = $result->fetch_row()[0];
    $result->free_result();
    return strcmp($userCred, $cred) === 0;
  } else {
    return false;
  }
}

if (strcmp($req->msgType, "LoginReq") === 0) {
  $reply->msgType = "LoginReply";
  $reply->cardNo = $req->cardNo;
  $reply->pin = $req->pin;
  $query = "SELECT PIN FROM CARD WHERE CARD_NUMBER='$req->cardNo'";
  if ($result = mysqli_query($conn, $query)) {
    $db_pin = $result->fetch_row()[0];
    if (strcmp($db_pin, $req->pin) === 0) {
      $cred = uniqid();
      $query = "UPDATE CARD SET CREDENTIAL='$cred' WHERE CARD_NUMBER='$req->cardNo'";
      if (mysqli_query($conn, $query)) {
        $reply->cred = $cred;
      } else {
        $reply->cred = "NOK";
      }
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
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->accounts = "CREDNOK";
  } else {
    $query = "SELECT GROUP_CONCAT(ACCOUNT_NO SEPARATOR '/') FROM ACCOUNT WHERE CARD_NUMBER='$req->cardNo'";
    if ($result = mysqli_query($conn, $query)) {
      $reply->accounts = $result->fetch_row()[0];
      $result->free_result();
    } else {
      $reply->accounts = "FAIL";
    }
  }
} else if (strcmp($req->msgType, "WithdrawReq") === 0) {
  $reply->msgType = "WithdrawReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->cred = "CREDNOK";
  } else {
    $query = "UPDATE ACCOUNT SET AMOUNT=AMOUNT-$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
    if (mysqli_query($conn, $query)) {
    } else {
      $reply->cred = "FAIL";
    }
    $reply->outAmount = $req->amount;
  }
} else if (strcmp($req->msgType, "DepositReq") === 0) {
  $reply->msgType = "DepositReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->cred = "CREDNOK";
  } else {
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
  }
} else if (strcmp($req->msgType, "EnquiryReq") === 0) {
  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->cred = "CREDNOK";
  } else {
    $query = "SELECT AMOUNT FROM ACCOUNT WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->accNo'";
    if ($result = mysqli_query($conn, $query)) {
      $reply->amount = $result->fetch_row()[0];
      $result->free_result();
    } else {
      $reply->amount = "FAIL";
    }
  }
} else if (strcmp($req->msgType, "TransferReq") === 0) {
  $reply->msgType = "TransferReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->fromAcc = $req->fromAcc;
  $reply->toAcc = $req->toAcc;
  $reply->amount = $req->amount;
  $reply->transAmount = $req->amount;
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->cred = "CREDNOK";
  } else {
    $query1 = "UPDATE ACCOUNT SET AMOUNT=AMOUNT-$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->fromAcc'";
    $query2 = "UPDATE ACCOUNT SET AMOUNT=AMOUNT+$req->amount WHERE CARD_NUMBER='$req->cardNo' AND ACCOUNT_NO='$req->toAcc'";
    if (mysqli_query($conn, $query1) && mysqli_query($conn, $query2)) {
    } else {
      $reply->cred = "FAIL";
    }
  }
} else if (strcmp($req->msgType, "ChgPinReq") === 0) {
  $reply->msgType = "ChgPinReply";
  $reply->cardNo = $req->cardNo;
  $reply->oldPin = $req->oldPin;
  $reply->newPin = $req->newPin;
  $reply->cred = $req->cred;
  $reply->result = "succ";
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->result = "ERROR";
  } else {
    $query1 = "UPDATE CARD SET PIN=$req->newPin WHERE CARD_NUMBER='$req->cardNo' AND PIN='$req->oldPin'";
    if (mysqli_query($conn, $query1)) {
      $reply->result = "succ";
    } else {
      $reply->result = "ERROR";
    }
  }
} else if (strcmp($req->msgType, "LogoutReq") === 0) {
  $reply->msgType = "LogoutReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
  if (!validateCred($conn, $req->cred, $req->cardNo)) {
    $reply->result = "ERROR";
  } else {
    $query1 = "UPDATE CARD SET CREDENTIAL=NULL WHERE CARD_NUMBER='$req->cardNo' AND CREDENTIAL='$req->cred'";
    if (mysqli_query($conn, $query1)) {
      $reply->result = "succ";
    } else {
      $reply->result = "ERROR";
    }
  }
} else if (strcmp($req->msgType, "AccStmtReq") === 0) {
  $reply->msgType = "AccStmtReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
} else if (strcmp($req->msgType, "ChqBookReq") === 0) {
  $reply->msgType = "ChqBookReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
} else if (strcmp($req->msgType, "ChgLangReq") === 0) {
  $reply->msgType = "ChgLangReply";
  $reply->cardNo = $req->cardNo;
  $reply->oldLang = $req->oldLang;
  $reply->newLang = $req->newLang;
  $reply->cred = $req->cred;
  $reply->result = "succ";
}

echo json_encode($reply);

@mysqli_close($conn);
