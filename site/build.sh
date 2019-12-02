#!/bin/bash

rm -rf public
mkdir public

echo "=== Generating hash ==="
./hash.sh ../app/src/main/assets/data/* ./*.sh ./*.html ./*.png > public/.hash
cat public/.hash
echo "=== Downloading hash from remote ==="
curl https://pbmap.termian.dev/.hash > .hash
cat .hash
echo "=== Comparing hashes ==="
DIFF=$(diff .hash public/.hash)
if [[ "$DIFF" == "" ]]
then
    echo "=== The hashes are equal, exiting ==="
    exit 0
fi
echo "=== The hashes are different, continuing the build ==="
cp logo.png public/logo.png
for file in ../app/src/main/assets/data/*
do
	echo "=== Processing $file ==="
	map=`./extract_ids.sh $file | head -n 1`
	#echo "Generating public/mobile/$map"
	mkdir -p "public/mobile/$map"
	rootUrl="https://play.google.com/store/apps/details?id=io.github.t3r1jj.pbmap\&referrer=https%3A%2F%2Fpbmap.termian.dev%2Fmobile"
	url="$rootUrl%2F$map"
	sed "s|%URL%|$url|g" template.html > "public/mobile/$map/index.html"
	for place in `./extract_ids.sh $file | tail -n+2 `
	do
	    #echo "Generating public/mobile/$map/$place"
    	mkdir -p "public/mobile/$map/$place"
	    url="$rootUrl%2F$map%2F$place"
		sed "s|%URL%|$url|g" template.html > "public/mobile/$map/$place/index.html"
	done
	sed "s|%URL%|$rootUrl|g" template.html > "public/mobile/index.html"
done

echo "Generating main index..."
cp index.html public/index.html
for file in public/*.html public/*/*.html public/*/*/*.html public/*/*/*/*.html;
do
    path=$(echo "$file" | cut -d/ -f2,3,4)
	place=$(echo "$path" | cut -d/ -f3)@
	space=$(echo "$path" | cut -d/ -f2)
	if [[ "$space" == "index.html" ]]
    then
        continue
    fi
	if [[ "$place" == "index.html@" ]]
    then
        place=""
    fi
    link="<li class=\"hide-default\"><a href=\"$path\">$place$space</a></li>"
    sed -i "s|%APPEND%|$link&|g" public/index.html
done
sed -i "s|%APPEND%||g" public/index.html
echo "Finished generating main index"