


let myArray=[2,7,0,9]


function travelArray(arr,n){
  if(arr.length==n){
    return ;
  }else{
    console.log(arr[n])
    travelArray(arr,n+1)
  }
}


travelArray(myArray,0)