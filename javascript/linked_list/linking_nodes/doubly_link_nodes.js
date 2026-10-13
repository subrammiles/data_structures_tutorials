let Node=require('./Node');

let l1=new Node(2);
let l2=new Node(34);
l1.nextNode=l2;
l2.previousNode=l1;
let l3=new Node(41);
l2.nextNode=l3;
l3.previousNode=l2;
l3.nextNode=null;

console.log(l1)

